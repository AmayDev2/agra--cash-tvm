package com.amay.tom.service.siftservice.impl;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controller.Controller;
import com.amay.tom.controller.EOSReport;
import com.amay.tom.controller.Maintenance;
import com.amay.tom.enums.*;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.adjust.AdjustedTicketDto;
import com.amay.tom.model.ccuRest.Role;
import com.amay.tom.model.refund.Refund;
import com.amay.tom.model.replacement.Replacement;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.session.ShiftDto;
import com.amay.tom.model.session.ShiftMapper;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.model.user.entity.UserPrivilege;
import com.amay.tom.pdu.controller.command.AbnormalStationModeCommand;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.service.base36.Base36Encoder;
import com.amay.tom.service.base36.ShiftIdGeneratorService;
import com.amay.tom.service.print.impl.ImplPrintTicket;
import com.amay.tom.service.siftservice.PopupContent;
import com.amay.tom.service.siftservice.ShiftService;
import com.amay.tom.service.userauth.UserAuth;
import com.amay.tom.utils.folder.NewFolder;
import com.amay.tom.utils.helper.Helper;
import com.amay.tom.utils.image.ImageUtils;
import com.amay.tom.utils.time.TimeUtil;
import com.amay.tvm.controller.TVMController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.amaytechnosystems.ShiftStatus;
import org.tinylog.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ShiftServiceImpl implements ShiftService {
    private final UserAuth userAuth;
    private final Agent agent;
    private final ShiftRepository shiftRepository;
    private Shift shift;
    private PopupContent popupContent;
    private Stage mainStage;

    public ShiftServiceImpl(Agent agent, UserAuth userAuth, ShiftRepository shiftRepository) {
        this.userAuth = userAuth;
        this.agent = agent;
        this.shiftRepository = shiftRepository;
    }


    @Override
    public FXMLLoader startShift(String username, String password) throws Exception {
        try (UserPrivilege userPrivilege = userAuth.login(username, password)) {

            this.agent.setUserPrivilege(userPrivilege);
            this.agent.setUserAuth(userAuth);
            int lastShiftSeq=getLastShiftIdFromServer();
            LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

            ShiftIdGeneratorService shiftIdGeneratorService= new ShiftIdGeneratorService(lastShiftSeq,shiftRepository, Base36Encoder.encode(Long.parseLong( TimeUtil.getCurrentDayPrefix()+SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())));

            String shiftId=shiftIdGeneratorService.getShiftId();


            shift = new Shift()
                    .setOperatorId(username)
                    .setShiftId(shiftId)
                    .setDeviceId(agent.getSystemConfig().getCurrentEquipment().getEquipmentId())
                    .setDeviceSerial(agent.getSystemConfig().getCurrentEquipment().getEquipmentSerial())
                    .setCreatedAt(currentTime)
                    .setStartTime(currentTime)
                    .setUpdatedAt(currentTime)
                    .setStationId(agent.getSystemConfig().getCurrentStation().getStationId())
                    .setLineNo(agent.getSystemConfig().getLineNumber())
                    .setCurrentStatus(ShiftStatus.ACTIVE.name())
                    .setConfig_version(agent.getMasterConfigInfo().getConfigVer());
             shiftRepository.startShift(ShiftMapper.toDto(shift));

            //   notifying to scu

            if(agent.getPeripheralMonitor().isCcu_connected()) {
                Logger.debug("Login data pushed to CC");
                CompletableFuture.runAsync(() -> agent.getCcuService().pushShiftInfo(shift), agent.getThreadPool().getFixedThreadPool());
                Logger.debug("CC responded to login data push");
            }

                //scu push
            if(agent.getPeripheralMonitor().isScu_connected()) {
                Logger.debug("Login data pushed to CC");
                CompletableFuture.runAsync(() -> agent.getScuService().pushShiftInfo(shift), agent.getThreadPool().getFixedThreadPool());
                Logger.debug("CC responded to login data push");
            }

            agent.setShift(shift);

            //  set orderId generator and ticketId generator
            agent.setShiftIdGeneratorService(shiftIdGeneratorService);
            agent.getShiftIdGeneratorService().setOrderIdGeneratorService();
            agent.getShiftIdGeneratorService().setTicketIdGeneratorService();


//            userAuth.getCurrentUser().getRoles().forEach(role -> {
//                System.out.println("Role: " + role);
//            });
            FXMLLoader fxmlLoader;
//            if(userAuth.hasRole(Role.MAINTENANCE.name())){
//                fxmlLoader= ViewFactory.getMaintenance();
//                fxmlLoader.setControllerFactory(controller -> new Maintenance(agent));
//                agent.getGrpcApiListener().sendAlarm(Alarm.MAINTENANCE_LOGIN);
//            }else {
                fxmlLoader = ViewFactory.getTVMHomeScreen();
                fxmlLoader.setControllerFactory(controller -> new TVMController(agent));
                agent.getGrpcApiListener().sendAlarm(Alarm.OPERATION_LOGIN);
//            }
            // load main screen
            return fxmlLoader;


        } catch (Exception usernameNotFoundException) {
            agent.getGrpcApiListener().sendAlarm(Alarm.LOGIN_FAILED);
            agent.getCcuGrpcApiListener().sendAlarm(Alarm.LOGIN_FAILED);
            throw  usernameNotFoundException;
        }
    }

    private int getLastShiftIdFromServer() {
        String scuShiftId = "00";
        String ccuShiftId = "00";
        try {
            Logger.debug("Last Shift request sent to SCU");
            scuShiftId=agent.getScuService().getTodayLastShiftId();
        } catch (RuntimeException e) {
            Logger.debug(e.getMessage());
        }
        try {
            Logger.debug("Last Shift request sent to CCU");
            ccuShiftId=agent.getCcuService().getTodayLastShiftId();
        } catch (RuntimeException e) {
            Logger.debug(e.getMessage());
        }

        return Math.max(getShiftSequence(scuShiftId),getShiftSequence(ccuShiftId));
    }

    private int getShiftSequence(String shiftId) {
        try {
            return Helper.getShiftSequence(shiftId);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void endOfShift(EOSType eosType) {
        LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        // update 4 columns endTime,endReason,updatedAt,status
        shift.setEndTime(currentTime)
                .setCurrentStatus(ShiftStatus.COMPLETED.name())
                .setUpdatedAt(currentTime)
                .setReason(eosType.name());

        shift.setImprest_money(String.valueOf(FareMedium.IMPREST_MONEY.getFareMediumTotal()));

        System.out.println("FareMedium : "+FareMedium.IMPREST_MONEY.getFareMediumTotal());

        // remove user privilege and user auth
        this.agent.setUserPrivilege(null);
        this.agent.setUserAuth(null);
        this.agent.setShiftIdGeneratorService(null);

        try {
            shiftRepository.endShift(ShiftMapper.toDto(shift)); //TODO: get complete shift
            Logger.debug("EOS : " + shift.toString());
            //notify to scu

            agent.getScuService().pushShiftEndAsync(shift,shiftRepository);
            agent.getCcuService().pushShiftEndAsync(shift,shiftRepository);
            new Thread(()-> PDUCommandDispatcher.INSTANCE.dispatch(new AbnormalStationModeCommand(DeviceOperationMode.SHIFT_NOT_ACTIVE))).start();

            this.printEOShift(this.shift.getShiftId());
            agent.getGrpcApiListener().sendAlarm(Alarm.SHIFT_END);
            //TODO: print end of Shift
            agent.getTomInitializerListener().setDeviceOperationMode(this.mainStage.getScene());

        } catch (Exception e) {
            //TODO: log
            agent.getGrpcApiListener().sendAlarm(Alarm.SHIFT_END_FAILED);
            e.printStackTrace();
        }finally {
            FareMedium.IMPREST_MONEY.setFareMediumTotal(0);
            FareMedium.NCMC.setFareMediumTotal(0);
            FareMedium.NCMC.setFareMediumSale(0);
            FareMedium.QR.setFareMediumSale(0);
        }
    }

    private void printEOShift(String shiftId) {
        AtomicReference<String> startTime = new AtomicReference<>();
        AtomicReference<String> endTime = new AtomicReference<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        shiftRepository.findById(shiftId).ifPresentOrElse(
                shift -> {
                    if(!String.valueOf(shift.getStartTime()).isBlank())
                    startTime.set(String.valueOf(shift.getStartTime().toLocalDateTime().format(formatter)).substring(0,16));
                    if(!String.valueOf(shift.getEndTime()).isBlank())
                    endTime.set(String.valueOf(shift.getEndTime().toLocalDateTime().format(formatter)).substring(0,16)) ;
                },
                () -> {
                    Logger.error("Shift not found for ID: " + shiftId);
                }
        );

        List<TicketsDto> list = agent.getTicketsRepository().getTicketByShiftId(shiftId);
        List<Refund> refundList = agent.getRefundTicketRepository().findByShiftId(shiftId);
        List<Replacement> replacementList = agent.getReplacementTicketRepository().findByShiftId(shiftId);
        String operatorId = shiftRepository.findOperatorIdByShiftId(shiftId);
        Logger.debug("Adjust Repo Agent Obj "+agent);
        Logger.debug("Adj Repo :"+agent.getAdjustedTicketRepository());
        List<AdjustedTicketDto> adjustedTicketList=agent.getAdjustedTicketRepository().findForEOS(shiftId);


        int noOfSJT = 0, qSJT = 0, amountSJT = 0;
        int noOfRJT = 0, qRJT = 0, amountRJT = 0;
        int noOfGroup = 0, qGroup = 0, amountGroup = 0;
        int noOfFree = 0, qFree = 0, amountFree = 0;
        int noOfPaid = 0, qPaid = 0, amountPaid = 0;
        int noOfCanceled = 0, qCanceled = 0, amountCanceled = 0;
        int noOfAdjusted = 0, qAdjusted = 0, amountAdjusted = 0;
        int noOfQrCashAdjusted=0, noOfQrUpiAdjusted=0, noOfQrPosAdjusted = 0;
        int noOfPaidExitCash=0, noOfPaidExitUpi=0, noOfPaidExitPos = 0;
        int noOfNcmcCashAdjusted=0, noOfNcmcUpiAdjusted=0, noOfNcmcPosAdjusted = 0;
        int qrCashAmountAdjusted = 0, qrUpiAmountAdjusted=0, qrPosAmountAdjusted = 0;
        int freeAdjustCount=0;
        int paidExitCashAmount=0, paidExitUpiAmount=0, paidExitPosAmount = 0;
        int ncmcCashAmountAdjusted=0, ncmcUpiAmountAdjusted=0, ncmcPosAmountAdjusted = 0;
        int noOfReplaced = 0, qReplaced = 0, amountReplaced = 0;
        int noOfRefund = 0, qRefunded = 0, amountRefunded = 0;
        LocalDateTime lastTransaction = null;
        int totalAmount = 0;
        int sjtRefundCount = 0, rjtRefundCount = 0, groupRefundCount = 0;
        int sjtRefundAmount=0, rjtRefundAmount = 0, groupRefundAmount = 0;
        int totalUpiTransactions=0,totalCardTransactions=0,totalCashTransactions=0;

        //adjust
        qAdjusted=adjustedTicketList.size();
        Set<String> set=new HashSet<>();
        for(AdjustedTicketDto adjustedTicket:adjustedTicketList){
            if(Integer.parseInt(adjustedTicket.getPenaltyAmount()==null?"0":adjustedTicket.getPenaltyAmount())==0){
                freeAdjustCount++;
            }else {
                amountAdjusted += Integer.parseInt(adjustedTicket.getPenaltyAmount() == null ? "0" : adjustedTicket.getPenaltyAmount());
                set.add(adjustedTicket.getAdjustId());// to count no if tickets
                if (Objects.equals(adjustedTicket.getPaymentMode(), PayMethod.CASH.name())) {
                    qrCashAmountAdjusted += Integer.parseInt(adjustedTicket.getPenaltyAmount() == null ? "0" : adjustedTicket.getPenaltyAmount());
                    noOfQrCashAdjusted++;
                } else if (Objects.equals(adjustedTicket.getPaymentMode(), PayMethod.UPI.name())) {
                    qrUpiAmountAdjusted += Integer.parseInt(adjustedTicket.getPenaltyAmount() == null ? "0" : adjustedTicket.getPenaltyAmount());
                    noOfQrUpiAdjusted++;
                } else if (Objects.equals(adjustedTicket.getPaymentMode(), PayMethod.POS.name())) {
                    qrPosAmountAdjusted += Integer.parseInt(adjustedTicket.getPenaltyAmount() == null ? "0" : adjustedTicket.getPenaltyAmount());
                    noOfQrPosAdjusted++;
                }
            }
        }
        noOfAdjusted= set.size();

        for (TicketsDto ticketsDto : list) {
            TicketType ticketType = TicketType.getTicket(ticketsDto.getTicketType());
            if (ticketType == null) {
                // Handle the invalid ticket type case, e.g., log it or continue
                continue;
            }

            switch (ticketType) {
                case SINGLE:
                    noOfSJT++;
                    qSJT += ticketsDto.getQuantity();
                    amountSJT += ticketsDto.getAmount();
                    break;
                case RETURN:
                    noOfRJT++;
                    qRJT += ticketsDto.getQuantity();
                    amountRJT += ticketsDto.getAmount();
                    break;
                case GROUP:
                    noOfGroup++;
                    qGroup += ticketsDto.getQuantity();
                    amountGroup += ticketsDto.getAmount();
                    break;
                case FREE:
                    noOfFree++;
                    qFree += ticketsDto.getQuantity();
                    amountFree += ticketsDto.getAmount();
                    break;
                case PAID:
                    noOfPaid++;
                    qPaid += ticketsDto.getQuantity();
                    amountPaid += ticketsDto.getAmount();
                    amountAdjusted+=ticketsDto.getAmount();
                    noOfAdjusted++;
                    if(Objects.equals(ticketsDto.getPaymentMode(), PayMethod.CASH.name())){
                        noOfPaidExitCash++;
                        paidExitCashAmount+=(int)ticketsDto.getAmount();
                    }else if(Objects.equals(ticketsDto.getPaymentMode(), PayMethod.UPI.name())){
                        noOfPaidExitUpi++;
                        paidExitUpiAmount+=(int)ticketsDto.getAmount();
                    }else if(Objects.equals(ticketsDto.getPaymentMode(), PayMethod.POS.name())){
                        noOfPaidExitPos++;
                        paidExitPosAmount+=(int)ticketsDto.getAmount();
                    }
                    break;
            }

            if (ticketsDto.isCanceled()) {
                noOfCanceled++;
                qCanceled += ticketsDto.getQuantity();
                amountCanceled -= ticketsDto.getAmount();
            }


//            if (ticketsDto.isRefund()) {
//                noOfRefund++;
//                qRefunded += ticketsDto.getQuantity();
//                amountRefunded -= ticketsDto.getAmount();
//                if(TicketType.SINGLE.getTicketTypeId().equals(ticketsDto.getTicketType())){
//                    sjtRefundCount++;
//                    sjtRefundAmount-=ticketsDto.getAmount();
//                } else if (TicketType.RETURN.getTicketTypeId().equals(ticketsDto.getTicketType())) {
//                    rjtRefundCount++;
//                    rjtRefundAmount-=ticketsDto.getAmount();
//                } else if(TicketType.GROUP.getTicketTypeId().equals(ticketsDto.getTicketType())) {
//                    groupRefundCount++;
//                    groupRefundAmount-=ticketsDto.getAmount();
//                }
//            }

//            if (ticketsDto.isAdjusted()) {
//                noOfAdjusted++;
//                qAdjusted += ticketsDto.getQuantity();
//                amountAdjusted += Integer.parseInt(ticketsDto.getAmount());
//            }

//            if (ticketsDto.isReplaced()) {
//                noOfReplaced++;
//                qReplaced += ticketsDto.getQuantity();
//                amountReplaced += ticketsDto.getAmount();
//            }

            // Track the last transaction time
            if (lastTransaction == null || ticketsDto.getCreatedAt().isAfter(lastTransaction)) {
                lastTransaction = ticketsDto.getCreatedAt();
            }
        }

        noOfReplaced = replacementList.size();

        for(Refund refund : refundList){
            noOfRefund++;
//            qRefunded += ticketsDto.getQuantity();
            amountRefunded -= (int) refund.getAmount();
            if(TicketType.SINGLE.getTicketTypeId().equals(refund.getTicketType())){
                sjtRefundCount++;
                sjtRefundAmount-= (int) refund.getAmount();
            } else if (TicketType.RETURN.getTicketTypeId().equals(refund.getTicketType())) {
                rjtRefundCount++;
                rjtRefundAmount-= (int) refund.getAmount();
            } else if(TicketType.GROUP.getTicketTypeId().equals(refund.getTicketType())) {
                groupRefundCount++;
                groupRefundAmount-= (int) refund.getAmount();
            }
        }

        //total cash transactions
        totalCashTransactions = amountGroup+amountRJT+amountSJT +qrCashAmountAdjusted+ncmcCashAmountAdjusted+paidExitCashAmount;

        // total sale amount
        totalAmount =totalCashTransactions+totalCardTransactions+totalUpiTransactions;

        // Output or further process the computed values as needed
        System.out.println("Single Journey Tickets: " + noOfSJT + " Quantity: " + qSJT + " Amount: " + amountSJT);
        System.out.println("Return Journey Tickets: " + noOfRJT + " Quantity: " + qRJT + " Amount: " + amountRJT);
        System.out.println("Group Tickets: " + noOfGroup + " Quantity: " + qGroup + " Amount: " + amountGroup);
        System.out.println("Free Tickets: " + noOfFree + " Quantity: " + qFree + " Amount: " + amountFree);
        System.out.println("Paid Tickets: " + noOfPaid + " Quantity: " + qPaid + " Amount: " + amountPaid);
        System.out.println("Canceled Tickets: " + noOfCanceled + " Quantity: " + qCanceled + " Amount: " + amountCanceled);
        System.out.println("Adjusted Tickets: " + noOfAdjusted + " Quantity: " + qAdjusted + " Amount: " + amountAdjusted);
        System.out.println("Replaced Tickets: " + noOfReplaced + " Quantity: " + qReplaced + " Amount: " + amountReplaced);
        System.out.println("Refund Tickets: " + noOfRefund + " Quantity: " + qRefunded + " Amount: " + amountRefunded);
        System.out.println("Last Transaction: " + lastTransaction);
        System.out.println("Imprest Money"+FareMedium.IMPREST_MONEY.getFareMediumTotal());
        System.out.println("Total Amount: " + totalAmount);
        System.out.println("Total Amount After Imprest Money: " + (totalAmount-FareMedium.IMPREST_MONEY.getFareMediumTotal()));

        FXMLLoader fxmlLoader = ViewFactory.getEOS();
        int finalAmountSJT = amountSJT;
        int finalNoOfSJT = noOfSJT;
        int finalNoOfRJT = noOfRJT;
        int finalAmountRJT = amountRJT;
        int finalNoOfGroup = noOfGroup;
        int finalAmountGroup = amountGroup;
        int finalNoOfFree = noOfFree;
        int finalAmountFree = amountFree;
        int finalNoOfPaid = noOfPaid;
        int finalAmountPaid = amountPaid;
        int finalAmountCanceled = amountCanceled;
        int finalNoOfCanceled = noOfCanceled;
        int finalNoOfAdjusted = noOfAdjusted;
        int finalAmountAdjusted = amountAdjusted;
        int finalNoOfReplaced = noOfReplaced;
        int finalAmountReplaced = amountReplaced;
        int finalNoOfRefund = noOfRefund;
        int finalAmountRefunded = amountRefunded;
        int finalTotalAmount = totalAmount ;
        int finalSubTotalCount = noOfSJT + noOfRJT + noOfGroup;
        int finalNoOfQrCashAdjusted = noOfQrCashAdjusted;
        int finalNoOfQrUpiAdjusted = noOfQrUpiAdjusted;
        int finalNoOfQrPosAdjusted = noOfQrPosAdjusted;

        int finalNoOfPaidExitCash = noOfPaidExitCash;
        int finalNoOfPaidExitUpi = noOfPaidExitUpi;
        int finalNoOfPaidExitPos = noOfPaidExitPos;

        int finalNoOfNcmcCashAdjusted = noOfNcmcCashAdjusted;
        int finalNoOfNcmcUpiAdjusted = noOfNcmcUpiAdjusted;
        int finalNoOfNcmcPosAdjusted = noOfNcmcPosAdjusted;

        int finalQrCashAmountAdjusted = qrCashAmountAdjusted;
        int finalQrUpiAmountAdjusted = qrUpiAmountAdjusted;
        int finalQrPosAmountAdjusted = qrPosAmountAdjusted;

        int finalPaidExitCashAmount = paidExitCashAmount;
        int finalPaidExitUpiAmount = paidExitUpiAmount;
        int finalPaidExitPosAmount = paidExitPosAmount;

        int finalNcmcCashAmountAdjusted = ncmcCashAmountAdjusted;
        int finalNcmcUpiAmountAdjusted = ncmcUpiAmountAdjusted;
        int finalNcmcPosAmountAdjusted = ncmcPosAmountAdjusted;

        int finalTotalCardTransactions = totalCardTransactions;
        int finalTotalUpiTransactions= totalUpiTransactions;
        int finalTotalCashTransactions=totalCashTransactions;

        int finalFreeAdjustCount = freeAdjustCount;

        LocalDateTime finalLastTransaction = lastTransaction==null ? LocalDateTime.now() : lastTransaction;
        String shiftStartTime= startTime.get();
        String shiftEndTime= endTime.get();
        try {
            VBox node=fxmlLoader.load();

            EOSReport eosReport = (EOSReport) fxmlLoader.getController();

         eosReport.setEOSReport(
                shiftId,
                finalNoOfSJT, finalAmountSJT,
                finalNoOfRJT, finalAmountRJT,
                finalNoOfGroup, finalAmountGroup,
                finalNoOfFree, finalAmountFree,
                finalNoOfPaid, finalAmountPaid,
                finalNoOfCanceled, finalAmountCanceled,
                finalNoOfAdjusted, finalAmountAdjusted,
                finalNoOfReplaced, finalAmountReplaced,
                finalNoOfRefund, finalAmountRefunded,
                finalLastTransaction,
                (FareMedium.IMPREST_MONEY.getFareMediumTotal()),
                agent, finalTotalAmount,finalSubTotalCount,
                SystemConfig.getInstance().getCurrentStation().getStationName(),
                shiftStartTime,
                shiftEndTime,
                 operatorId,sjtRefundCount,sjtRefundAmount,
                 rjtRefundCount,rjtRefundAmount,groupRefundCount,groupRefundAmount,
                 finalNoOfQrCashAdjusted,
                 finalNoOfQrUpiAdjusted,
                 finalNoOfQrPosAdjusted,
                 finalNoOfPaidExitCash,
                 finalNoOfPaidExitUpi,
                 finalNoOfPaidExitPos,
                 finalNoOfNcmcCashAdjusted,
                 finalNoOfNcmcUpiAdjusted,
                 finalNoOfNcmcPosAdjusted,
                 finalQrCashAmountAdjusted,
                 finalQrUpiAmountAdjusted,
                 finalQrPosAmountAdjusted,
                 finalPaidExitCashAmount,
                 finalPaidExitUpiAmount,
                 finalPaidExitPosAmount,
                 finalNcmcCashAmountAdjusted,
                 finalNcmcUpiAmountAdjusted,
                 finalNcmcPosAmountAdjusted,
                 finalTotalCardTransactions,
                 finalTotalUpiTransactions,
                 finalTotalCashTransactions,
                 finalFreeAdjustCount
         );

            BufferedImage bufferedImage = ImageUtils.nodeToImage(node);
            String folderPath = NewFolder.createTodayFolder();
            ImageUtils.saveBufferedImage(bufferedImage, folderPath + "\\" + "shift_" + shiftId  + ".png");
//            ImplPrintTicket.printImage(bufferedImage);

        } catch (RuntimeException | IOException e) {
            Logger.info("Could not save image for EOS report: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void pauseShift() {
        System.out.println("sunliyaa");
        agent.getGrpcApiListener().sendAlarm(Alarm.SHIFT_PAUSE);
        LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        // update 4 columns endTime,endReason,updatedAt,status
        shift.setCurrentStatus(ShiftStatus.PAUSED.name())
                .setUpdatedAt(currentTime);

        // remove user privilege and user auth
        this.agent.setUserPrivilege(null);
        this.agent.setUserAuth(null);

        try{shiftRepository.shiftPauseResume(ShiftMapper.toDto(shift));
            //notify to scu
            agent.getScuService().pushShiftPause(shift);

            //popup
            popupContent = new PopupContent(this);
            popupContent.show();
        }catch (Exception e){
            //TODO: log
            e.printStackTrace();
        }

    }

    @Override
    public void resumeShift(String password) {
        agent.getGrpcApiListener().sendAlarm(Alarm.ATTEMPT_SHIFT_RESUME);
        try(UserPrivilege userPrivilege=userAuth.login(userAuth.getCurrentUser().getUsername(),password)) {

            LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
            // update 4 columns endTime,endReason,updatedAt,status
            shift.setCurrentStatus(ShiftStatus.ACTIVE.name()).setUpdatedAt(currentTime);
            shiftRepository.shiftPauseResume(ShiftMapper.toDto(shift));
            agent.getScuService().resumeShiftPause(shift);

            this.agent.setUserPrivilege(userPrivilege);
            this.agent.setUserAuth(userAuth);

//
//            agent.getThreadPool().getScheduler().schedule(() -> {
//                try {
//                    this.endOfShift(EOSType.OPERATOR);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }, 5, TimeUnit.SECONDS);

            //TODO: UPDATE UI
            popupContent.Close();
            agent.getGrpcApiListener().sendAlarm(Alarm.SHIFT_RESUME);


        }catch (Exception e){
            agent.getGrpcApiListener().sendAlarm(Alarm.SHIFT_RESUME_FAILED);
            popupContent.setError("wrong credential");
        }
    }

    @Override
    public void setMainStage(Stage mainStage) {
        this.mainStage=mainStage;

    }

    @Override
    public Optional<String> checkLastShiftCompletion() {
            return  shiftRepository.findLastUncompletedShiftId();
    }

    @Override
    public void markLastShiftAsCompleted(String shiftId) {
        LocalDateTime localDateTime= LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        ShiftDto shiftDto=new ShiftDto()
                .setShiftId(shiftId)
                .setCurrentStatus(ShiftStatus.COMPLETED.name())
                .setEndTime(Timestamp.valueOf(localDateTime))
                .setUpdatedAt(Timestamp.valueOf(localDateTime))
                .setReason(EOSType.LAST_SHIFT.name());

        this.shiftRepository.markLastShiftAsCompleted(shiftDto);

        Shift shift=new Shift().setEndTime(localDateTime)
                .setShiftId(shiftDto.getShiftId())
                .setCurrentStatus(shiftDto.getCurrentStatus())
                .setUpdatedAt(localDateTime)
                .setReason(shiftDto.getReason());

        this.agent.getThreadPool().getFixedThreadPool().execute(() -> {
            try {
                this.agent.getScuService().pushShiftEnd(shift);
                this.agent.getCcuService().pushShiftEnd(shift);
                Logger.info("Last Shift marked as completed with ID: {}", shiftId);
                this.agent.getGrpcApiListener().sendAlarm(Alarm.SHIFT_END);
            } catch (Exception e) {
                Logger.error("Error printing EOS report for last shift: {}", e.getMessage());
            }
        });


    }

    @Override
    public void printEOSReport(String shiftId) {
        this.printEOShift(shiftId);
        agent.getGrpcApiListener().sendAlarm(Alarm.SHIFT_END);
    }
}
