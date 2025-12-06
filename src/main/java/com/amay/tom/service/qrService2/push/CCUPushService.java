package com.amay.tom.service.qrService2.push;

import com.amay.tom.agent.Agent;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.model.adjust.AdjustedTicketDto;
import com.amay.tom.model.adjust.AdjustedTicketMapper;
import com.amay.tom.model.refund.Refund;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.session.ShiftDto;
import com.amay.tom.model.session.ShiftMapper;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.refund.RefundTicketRepository;
import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.repository.session.ShiftRepositoryImpl;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.service.qrService2.push.PushService;
import com.amay.tvm.backend.entity.AmountSnapShotEntity;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.enums.ContainerId;
import com.amay.tvm.backend.mapper.CoinAmountMapper;
import com.amay.tvm.backend.mapper.NoteAmountMapper;
import com.amay.tvm.backend.service.CashInventoryService;
import com.amay.tvm.backend.service.FinanceOperationService;
import lombok.extern.slf4j.Slf4j;
import org.amaytechnosystems.*;
import org.tinylog.Logger;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class CCUPushService implements PushService {
    private final Agent agent;
    private String currentShiftId;
    private final String CHANAL="ccu";
    public CCUPushService(Agent agent){
        this.agent=agent;
    }
    @Override
    public void push() {
        AtomicBoolean isSuccess= new AtomicBoolean(false);
        ArrayList<String> listOfShifts=new ArrayList<>();
        ArrayList<String> listOfTickets=new ArrayList<>();
        ArrayList<String> listOfRefunds = new ArrayList<>();
        ArrayList<String> listOfAdjusts = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ShiftRepository shiftRepository = new ShiftRepositoryImpl(agent.getConnection());
        AdjustedTicketRepository adjustedTicketsRepository = agent.getAdjustedTicketRepository();
        TicketsRepository ticketsRepository = agent.getTicketsRepository();
        RefundTicketRepository refundTicketRepository = agent.getRefundTicketRepository();

        // Push the remained data to the server
//        LastOnline lastOnline = (LastOnline) FileSerializeService.retrieveData("lastUpdated.ser", EnvFile.getLastUpdatedFile());
//        if (lastOnline == null) {
//            Logger.debug("LastOnline is null");
//            lastOnline = new LastOnline().setCCU(Instant.now().minus(Duration.ofDays(30))).setSCU(Instant.now().minus(Duration.ofDays(30)));
//        }
//        Logger.debug("Last updated time: " + lastOnline.getCCU()+ " " + lastOnline.getSCU());
        // 1- Fetch SCU not pushed  entries
        // Query-> dto -> proto object -> push to server
        //2- push shift information SCU
        // 3- push Generated Tickets SCU



        // TICKET ISSUED DATA PUSH
        pushTickets(ticketsRepository,futures,listOfTickets);
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        ticketsRepository.pushTickets(listOfTickets,CHANAL);
        // SHIFT DATA PUSH
        pushShifts(shiftRepository,futures,listOfShifts);
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        shiftRepository.pushShifts(listOfShifts,CHANAL);
        // DONE:DEV4:- Add ADJUSTMENT + REFUND also to push data on server similar to shift and ticket
        // REFUNDED DATA PUSH
        pushRefunds(refundTicketRepository,futures,listOfRefunds);
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        refundTicketRepository.pushRefunds(listOfRefunds,CHANAL);
        // ADJUSTED DATA PUSH
        pushAdjusts(adjustedTicketsRepository,futures,listOfAdjusts);

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        Logger.debug("All push operations completed successfully: {}", isSuccess.get());
        adjustedTicketsRepository.pushTickets(listOfAdjusts,CHANAL);






        // 4- push adjusted, refunded, canceled, replaced on scu
//        AdjustedTicketRepository adjustedTicketRepository = agent.getAdjustedTicketRepository();
//        List<AdjustedTicketDto> adjustedTickets = adjustedTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
//        RefundTicketRepository refundTicketRepository = new RefundTicketRepositoryImpl(agent.getConnection(),agent.getTicketsRepository());
//        List<Refund> refundedTickets = refundTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
        //TODO: Add canceled and replaced tickets
//        if(isSuccess.get())FileSerializeService.saveData("lastUpdated.ser", lastOnline.setSCU(Instant.now()),EnvFile.getLastUpdatedFile());



    }
    private void markShiftSyncStatus(Shift shift,boolean status){
        if(Objects.equals(shift.getShiftId(), currentShiftId)){
            if(status) agent.getShift().setCcu(Timestamp.valueOf(LocalDateTime.now()));
            else agent.getShift().setCcu(null);
        }
    }
    private void pushShifts(ShiftRepository shiftRepository,List<CompletableFuture<Void>> futures, List<String> listOfShifts){
        List<ShiftDto> shiftDtos=shiftRepository.findNotPushedShifts(CHANAL);
        if(agent.getShift()!=null)
            currentShiftId=agent.getShift().getShiftId();
        if(shiftDtos.isEmpty()){
            Logger.debug("No shift data found");
        }else {
            Logger.debug("Shift data found: {} {}", shiftDtos.toString(), shiftDtos.size());
        }
        for(ShiftDto dbShift: shiftDtos){
            Shift shift = new Shift()
                    .setOperatorId(dbShift.getOperatorId())
                    .setShiftId(dbShift.getShiftId())
                    .setDeviceId(dbShift.getDeviceId())
                    .setDeviceSerial(dbShift.getSerialNo())
                    .setCreatedAt(dbShift.getCreatedAt().toLocalDateTime())
                    .setStartTime(dbShift.getStartTime().toLocalDateTime())
                    .setUpdatedAt(dbShift.getUpdatedAt().toLocalDateTime())
                    .setStationId(dbShift.getStationId())
                    .setLineNo(dbShift.getLineNo())
                    .setCurrentStatus(ShiftStatus.COMPLETED.name())
                    .setReason(dbShift.getReason());
            if(dbShift.getEndTime()!=null){
                shift.setEndTime(dbShift.getEndTime().toLocalDateTime());
            }else {
                shift.setCurrentStatus(ShiftStatus.COMPLETED.name());
                shiftRepository.markLastShiftAsCompleted(ShiftMapper.toDto(shift));
                this.updateCashInventory(shift.getShiftId());
            }

            CashInventoryService cashInventoryService = new CashInventoryService(agent.getAmountSnapShotRepository(),agent.getTvmConfig());
            List<AmountSnapShotEntity> amountSnapShotEntityList = cashInventoryService.getCashInventoryByShiftId(dbShift.getShiftId());
            shift.setAmountSnapShotEntityList(amountSnapShotEntityList);

            FinanceOperationService financeOperationService = new FinanceOperationService(agent.getFinanceOperationRepository(), agent.getTvmConfig());
            List<FinanceOperationEntity> financeOperationEntityList = financeOperationService.getFinanceOperationEntityLoadUnloadListByShiftId(dbShift.getShiftId());
            shift.setFinanceOperationEntityList(financeOperationEntityList);

//            if(shift.getCurrentStatus()==ShiftStatus.)
//            if(agent.getPeripheralMonitor().isCcu_connected()) CompletableFuture.runAsync(() -> agent.getCcuService().pushShiftInfo(shift),agent.getThreadPool().getFixedThreadPool());
            futures.add( CompletableFuture.runAsync(() -> {
                try {
                    ShiftResponseV1 shiftResponse=agent.getCcuService().pushShiftInfoBulk(shift);
                    if(shiftResponse.getResponseMetaData().getErrorCode().equals("200") || shiftResponse.getResponseMetaData().getErrorCode().equals("751")){
                        markShiftSyncStatus(shift,true);
                        Logger.debug("PUSHED DATA: {} {}",shift.toString(),shift.getCurrentStatus());
                        if(shift.getCurrentStatus().equals(ShiftStatus.COMPLETED.name()))
                            listOfShifts.add(shift.getShiftId());
                    } else if (shiftResponse.getResponseMetaData().getErrorCode().equals("752")) {
                        markShiftSyncStatus(shift,false);
                        shift.setCurrentStatus(ShiftStatus.ACTIVE.name());
                        if(agent.getShift()==null)
                        {
                        shift.setCurrentStatus(ShiftStatus.COMPLETED.name());
                        shift.setEndTime(LocalDateTime.now());
                        }
                        shiftResponse=agent.getCcuService().pushShiftInfoBulk(shift);
                        if(shiftResponse.getResponseMetaData().getErrorCode().equals("200")){
                            listOfShifts.add(shift.getShiftId());
                        }
                    } else if (shiftResponse.getResponseMetaData().getErrorCode().equals("753")) {
                        markShiftSyncStatus(shift,false);
                        if(agent.getShift()==null)
                        {
                            shift.setCurrentStatus(ShiftStatus.COMPLETED.name());
                            shift.setEndTime(LocalDateTime.now());
                            shiftResponse=agent.getCcuService().pushShiftInfoBulk(shift);
                            if(shiftResponse.getResponseMetaData().getErrorCode().equals("200")){
                                markShiftSyncStatus(shift,true);
                                listOfShifts.add(shift.getShiftId());
                            }
                        }
                        log.error("pushShifts : End time mismatch");
                    }

                } catch (Exception e) {
                    Logger.error("Error pushing shift info to CCU: {}", e.getMessage(), e);
                }
            },agent.getThreadPool().getFixedThreadPool()));
        }
    }

    private void pushTickets(TicketsRepository ticketsRepository, List<CompletableFuture<Void>> futures, List<String> listOfTickets){
        List<TicketsDto> tickets = ticketsRepository.findNotPushedTicket(CHANAL);
        if (tickets.isEmpty()) {
            Logger.debug("No tickets data found");
        }else {
            Logger.debug("Tickets data found: {} {}", tickets.toString(), tickets.size());
        }

        for (TicketsDto ticket : tickets) {
            TicketRequestV1 ticketRequestV1 = ScuDataMapper.createTicketIssueRequest(ticket);
//            if(agent.getPeripheralMonitor().isCcu_connected())CompletableFuture.runAsync(() -> agent.getCcuService().pushTicketIssueInfo(ticketRequestV1),agent.getThreadPool().getFixedThreadPool());
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    TicketResponseV1 ticketResponse= agent.getCcuService().pushTicketIssueInfoBulk(ticketRequestV1);
                    if(ticketResponse.getResponseMetaData().getErrorCode().equals("200")){
                        listOfTickets.add(ticketRequestV1.getTicketData().getTicket().getTicketId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.error("Error pushing ticket issue info to CCU: {}", e.getMessage(), e);
                }
            },agent.getThreadPool().getFixedThreadPool()));


        }
    }

    private void pushRefunds(RefundTicketRepository refundTicketRepository,List<CompletableFuture<Void>> futures, List<String> listOfRefunds){
        List<Refund> refunds = refundTicketRepository.findNotPushedTicket(CHANAL);
        if (refunds.isEmpty()) {
            Logger.debug("No tickets data found");
        }else {
            Logger.debug("Tickets data found: {} {}", refunds.toString(), refunds.size());
        }

        for (Refund refund : refunds) {
            TicketRefundRequestV1 refundRequestV1 = ScuDataMapper.createTicketRefundPushRequestByNumber(refund.getTicketNumber(), refund.getRefundMode(), (int)refund.getAmount(), refund.getTicketType(), refund.getRefundId(), refund.getOperatorId(), refund.getShiftId());
//            if(agent.getPeripheralMonitor().isCcu_connected())CompletableFuture.runAsync(() -> agent.getCcuService().pushTicketIssueInfo(ticketRequestV1),agent.getThreadPool().getFixedThreadPool());
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    TicketRefundResponseV2 refundResponseV2= agent.getCcuService().refundTicket(refundRequestV1);
                    if(refundResponseV2.getResponseMetaData().getErrorCode().equals("200")){
                        listOfRefunds.add(refundRequestV1.getTicketRefundData().getRefundInfo().getRefundId());
                    }
                } catch (Exception e) {
                    Logger.error("Error pushing ticket issue info to CCU: {}", e.getMessage(), e);
                }
            },agent.getThreadPool().getFixedThreadPool()));


        }
    }

    private void pushAdjusts(AdjustedTicketRepository adjustedTicketsRepository,List<CompletableFuture<Void>> futures, List<String> listOfAdjusts){
        List<AdjustedTicketDto> adjustedTicketDtos = adjustedTicketsRepository.findNotPushedTicket(CHANAL);
        if (adjustedTicketDtos.isEmpty()) {
            Logger.debug("No tickets data found");
        }else {
            Logger.debug("Tickets data found: {} {}", adjustedTicketDtos.toString(), adjustedTicketDtos.size());
        }

        for (AdjustedTicketDto adjustedTicketDto : adjustedTicketDtos) {
            TicketAdjustedRequestV1 adjustedRequestV1 = ScuDataMapper.createAdjustTicketPushRequest(adjustedTicketDto.getOrderId(), adjustedTicketDto.getTransactionId(), AdjustedTicketMapper.toEntity(adjustedTicketDto));
//            if(agent.getPeripheralMonitor().isCcu_connected())CompletableFuture.runAsync(() -> agent.getCcuService().pushTicketIssueInfo(ticketRequestV1),agent.getThreadPool().getFixedThreadPool());
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    TicketAdjustedResponseV1 adjustedResponseV1= agent.getCcuService().pushTicketAdjustedData(adjustedRequestV1); // pushTicketAdjustedData used to push data but not to adjust
                    if(adjustedResponseV1.getResponseMetaData().getErrorCode().equals("200")){
                        listOfAdjusts.add(adjustedRequestV1.getTicketData().getAdjustedTicket().getAdjustId());
                    }

                } catch (Exception e) {
                    Logger.error("Error pushing ticket issue info to CCU: {}", e.getMessage(), e);
                }
            },agent.getThreadPool().getFixedThreadPool()));


        }
    }
    private void updateCashInventory(String shiftId) {
        agent.getNoteAmountRepository().findAll().stream().filter(noteAmountEntity
                -> noteAmountEntity.getContainerId().equals(ContainerId.CB)).forEach(noteAmount -> {
            agent.getAmountSnapShotRepository().save(NoteAmountMapper.toSnapshot(noteAmount,shiftId));

        });

        agent.getCoinAmountRepository().findAll().forEach(coinAmount -> {
            agent.getAmountSnapShotRepository().save(CoinAmountMapper.toSnapshot(coinAmount,shiftId));
        });
    }
        // 4- push adjusted, refunded, canceled, replaced on scu
//        AdjustedTicketRepository adjustedTicketRepository = agent.getAdjustedTicketRepository();
//        List<AdjustedTicketDto> adjustedTickets = adjustedTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
//        RefundTicketRepository refundTicketRepository = new RefundTicketRepositoryImpl(agent.getConnection(),agent.getTicketsRepository());
//        List<Refund> refundedTickets = refundTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
        //TODO: Add canceled and replaced tickets

//        if(isSuccess.get())FileSerializeService.saveData("lastUpdated.ser", lastOnline.setCCU(Instant.now()),EnvFile.getLastUpdatedFile());


    }
