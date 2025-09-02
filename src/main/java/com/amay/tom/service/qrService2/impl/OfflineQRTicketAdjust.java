package com.amay.tom.service.qrService2.impl;


import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.adjust.AdjustedTicket;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.model.session.Shift;
import com.amay.tom.pdu.controller.PaymentSummary;
import com.amay.tom.service.base36.TransactionIdGeneratorService;
import com.amay.tom.service.qrService2.QRTicketAdjustment;
import com.amay.tom.service.qrService2.TicketInfo;
import com.amay.tom.service.qrservice.Impl.ImplQRService;
import com.amay.tom.service.qrservice.QRService;
import com.amay.tom.utils.encription.Base64Encoding;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*1:- ticketIds generation  --off(Getting the id instead of orderId)*/
//2:- ticketQRs string generation --off
//3:- ticketQRs string encryption --off
//3.5 save into db
//3.6 push on scu
//4:- ticket's QR image generation
//5:- ticket's QR image saving with id
//6:- ticket image generation
//7:- ticket image saving with id
//8:- receipt image generation
//9:- ticket printing
//10:- receipt generation
public class OfflineQRTicketAdjust extends QRTicketAdjustment {

    private static final String delimiter = ":";
    private final QRService qrService;
//    private ImplTicketService implTicketService = null;

    public OfflineQRTicketAdjust(Agent agent) {
        super(agent.getAdjustedTicketRepository(),agent.getScuService(),agent.getShift(),agent.getThreadPool(), agent.getCcuService());
        qrService = new ImplQRService();
    }

    @Override
    public ArrayList<GeneratedTicket> processTicket(String OrderId, String transactionId, TicketInfo ticketInfo) {
        AdjustedTicket ticket = null;
        ticket=this.adjustTicket(OrderId, transactionId, ticketInfo);
        if(null!=ticket){
//            ticket.setPaymentMode(ticketInfo.getPreGeneratadTicket().getPaymentResponse().getPaymentMode());
            ticket.setAdjustId(UUID.randomUUID().toString());
            String adjustId=super.saveIntoDb(ticket);
            ticket.setTicketInfo(ticketInfo);
            super.pushToScuAsync(OrderId,transactionId,ticket);
            Logger.getLogger("OfflineQRTicketAdjust").info("Ticket Adjusted with id: " + adjustId);
        }

        GeneratedTicket finalTicket = ticket;
        return new ArrayList<GeneratedTicket>() {{
            add(finalTicket);
        }};
    }


    public AdjustedTicket adjustEntryExitOverride(String OrderId, String transactionId, TicketInfo ticketInfo) {

        //1-generate QR String
        //2-encrypt QR String
        //TODO:TEST- set entry time in ticketInfo before encryption
//        ticketInfo.getQrTicketV2().setIssueAt(Instant.now().toEpochMilli());

        String qrData= Base64Encoding.decode(ticketInfo.getQrData());
        String[] qrDataArray=qrData.split(delimiter);
        ticketInfo.setEffectiveTime(Instant.now().toEpochMilli());
        //index 2
        qrDataArray[2]= String.valueOf(ticketInfo.getEffectiveTime());
        ticketInfo.setQrData(Arrays.toString(qrDataArray));

        String encryptQR = this.getQRDataByTicketV1(ticketInfo.getQrData(),ticketInfo.getQrTicketV2().getTicketId(),ticketInfo.getQrTicketV2().getInStation().getStationId(),ticketInfo.getQrTicketV2().getOutStation().getStationId(),
                ticketInfo.getQrTicketV2().getAmount(),ticketInfo.getQrTicketV2().getOperatorId(),ticketInfo.getQrTicketV2().getOperatorId(),ticketInfo.getQrTicketV2().getOperatorId(),
                String.valueOf(ticketInfo.getQrTicketV2().getIssueAt()),String.valueOf(ticketInfo.getQrTicketV2().getValidUntil()),Integer.parseInt(ticketInfo.getQrTicketV2().getTicketType().getTicketTypeId()),ticketInfo.getQrTicketV2().getQuantity());

        AdjustedTicket adjustedTicket = getAdjustedTicket(ticketInfo, encryptQR).setOrderId(OrderId)
                .setTransactionId(transactionId)
                .setAdjustmentType("ENTRY_TIME")
                .setDestination(ticketInfo.getQrTicketV2().getOutStation().getStationId())
                .setIssueTime(String.valueOf(ticketInfo.getEffectiveTime()))
                .setPaymentMode(ticketInfo.getQrTicketV2().getFareMode());
        //3-save into db
        String adjustId = saveIntoDb(adjustedTicket);

        Logger.getLogger("OfflineQRTicketAdjust").info("Ticket Adjusted with id: " + adjustId);
//        getQRImage(encryptQR);
        return adjustedTicket;

    }




    //NOTE:- here we are not generating any new qrData
    //1- generate an adjusted id
    @Override
    public AdjustedTicket adjustTicket(String OrderId, String transactionId, TicketInfo ticketInfo) {
        AdjustedTicket adjustedTicket = getAdjustedTicket(ticketInfo, ticketInfo.getQrData()).setOrderId(OrderId)
                .setTransactionId(transactionId)
                .setTicketInfo(ticketInfo);
        return adjustedTicket;

    }

    @Override
    public AdjustedTicket adjustEntryTimeOverride(String OrderId, String transactionId, TicketInfo ticketInfo) {
       
        //1-generate QR String
        //2-encrypt QR String
        //TODO:TEST- set entry time in ticketInfo before encryption
//        ticketInfo.getQrTicketV2().setIssueAt(Instant.now().toEpochMilli());

        String qrData= Base64Encoding.decode(ticketInfo.getQrData());
        String[] qrDataArray=qrData.split(delimiter);
        ticketInfo.setEffectiveTime(Instant.now().toEpochMilli());
        //index 2
        qrDataArray[2]= String.valueOf(ticketInfo.getEffectiveTime());
        ticketInfo.setQrData(Arrays.toString(qrDataArray));
        
        String encryptQR = this.getQRDataByTicketV1(ticketInfo.getQrData(),ticketInfo.getQrTicketV2().getTicketId(),ticketInfo.getQrTicketV2().getInStation().getStationId(),ticketInfo.getQrTicketV2().getOutStation().getStationId(),
                ticketInfo.getQrTicketV2().getAmount(),ticketInfo.getQrTicketV2().getOperatorId(),ticketInfo.getQrTicketV2().getOperatorId(),ticketInfo.getQrTicketV2().getOperatorId(),
                String.valueOf(ticketInfo.getQrTicketV2().getIssueAt()),String.valueOf(ticketInfo.getQrTicketV2().getValidUntil()),Integer.parseInt(ticketInfo.getQrTicketV2().getTicketType().getTicketTypeId()),ticketInfo.getQrTicketV2().getQuantity());

        AdjustedTicket adjustedTicket = getAdjustedTicket(ticketInfo, encryptQR).setOrderId(OrderId)
                .setTransactionId(transactionId)
                .setAdjustmentType("ENTRY_TIME")
                .setDestination(ticketInfo.getQrTicketV2().getOutStation().getStationId())
                .setIssueTime(String.valueOf(ticketInfo.getEffectiveTime()));

        //3-save into db
        String adjustId = saveIntoDb(adjustedTicket);

        Logger.getLogger("OfflineQRTicketAdjust").info("Ticket Adjusted with id: " + adjustId);
//        getQRImage(encryptQR);
        return adjustedTicket;

    }

    @Override
    public AdjustedTicket adjustExitTimeOverride(String OrderId, String transactionId, TicketInfo ticketInfo) {
       
        //1-generate QR String
        //2-encrypt QR String

        //TODO: HACK: (15*60*100) make this using config
        String qrData= Base64Encoding.decode(ticketInfo.getQrData());
        String[] qrDataArray=qrData.split(delimiter);
        qrDataArray[3]= String.valueOf(ticketInfo.getExitExtendedDurationInMin()*60*100);
        qrDataArray[3]= String.valueOf(Instant.now().toEpochMilli()+(15*60*100)); //Remove this line
        ticketInfo.setQrData(Arrays.toString(qrDataArray));

        String encryptQR =  this.getQRDataByTicketV1(ticketInfo.getQrData(),ticketInfo.getQrTicketV2().getTicketId(),ticketInfo.getQrTicketV2().getInStation().getStationId(),ticketInfo.getQrTicketV2().getOutStation().getStationId(),
                ticketInfo.getQrTicketV2().getAmount(),ticketInfo.getQrTicketV2().getOperatorId(),ticketInfo.getQrTicketV2().getOperatorId(),ticketInfo.getQrTicketV2().getOperatorId(),
                String.valueOf(ticketInfo.getQrTicketV2().getIssueAt()),String.valueOf(ticketInfo.getQrTicketV2().getValidUntil()),Integer.parseInt(ticketInfo.getQrTicketV2().getTicketType().getTicketTypeId()),ticketInfo.getQrTicketV2().getQuantity());



        AdjustedTicket adjustedTicket = getAdjustedTicket(ticketInfo, encryptQR)
                .setOrderId(OrderId).setTransactionId(transactionId)
                .setAdjustmentType("EXIT_TIME")
                .setDestination(ticketInfo.getQrTicketV2().getOutStation().getStationId())
                .setExitTime(String.valueOf(ticketInfo.getTime()));

        //3-save into db
        String adjustId = saveIntoDb(adjustedTicket);

        Logger.getLogger("OfflineQRTicketAdjust").info("Ticket Adjusted with id: " + adjustId);
        return adjustedTicket;

    }

    @Override
    public AdjustedTicket adjustDestinationOverride(String OrderId, String transactionId, TicketInfo ticketInfo) {
//        Station station = StationData.getInstance().getStation("07");
//        ticketInfo.getMetroTicket().setDestination(station);
//       
        //1-generate QR String
        //2-encrypt QR String
          //TODO: set destination in ticketInfo before encryption
        String qrData= Base64Encoding.decode(ticketInfo.getQrData());
        String[] qrDataArray=qrData.split(delimiter);
        qrDataArray[6]= ticketInfo.getDestination().getStationId();
        ticketInfo.setQrData(Arrays.toString(qrDataArray));

        String encryptQR =  this.getQRDataByTicketV1(ticketInfo.getQrData(),ticketInfo.getQrTicketV2().getTicketId(),ticketInfo.getQrTicketV2().getInStation().getStationId(),ticketInfo.getQrTicketV2().getOutStation().getStationId(),
                ticketInfo.getQrTicketV2().getAmount(),ticketInfo.getQrTicketV2().getOperatorId(),ticketInfo.getQrTicketV2().getOperatorId(),ticketInfo.getQrTicketV2().getOperatorId(),
                String.valueOf(ticketInfo.getQrTicketV2().getIssueAt()),String.valueOf(ticketInfo.getQrTicketV2().getValidUntil()),Integer.parseInt(ticketInfo.getQrTicketV2().getTicketType().getTicketTypeId()),ticketInfo.getQrTicketV2().getQuantity());


        //QR Ticket
        return getAdjustedTicket(ticketInfo, encryptQR).setOrderId(OrderId)
                .setTransactionId(transactionId)
                .setAdjustmentType("DESTINATION")
                .setDestination(ticketInfo.getQrTicketV2().getOutStation().getStationId())
                .setPenaltyAmount(String.valueOf(ticketInfo.getPAmount()));

    }

    private AdjustedTicket getAdjustedTicket(TicketInfo ticketInfo, String encryptQR) {
        return new AdjustedTicket()
                .setArea(ticketInfo.getArea())
                .setPenaltyAmount(String.valueOf(ticketInfo.getPAmount()))
                .setTicketNumber(ticketInfo.getQrTicketV2().getTicketId())
                .setAdjustmentType(ticketInfo.getAdjustmentType().stream().map(Enum::name).collect(Collectors.joining(", ")))
                .setEncryptedQR(encryptQR).setCreatedAt(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault())).setUpdatedAt(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()))
                .setTransactionTime(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()))
                .setDeviceId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId());
    }


    private String getQRDataByTicketV1(
            String oldQRData,
            String ticketId,String source, String destination,
            int fare, String lineNumber, String stationId, String equipmentSerial,
            String issuanceTime, String validityTime, int ticketTypeId, int ticketQuantity) {

//        if(ticketId == null || ticketId.isEmpty()) {
//            throw new TicketNotGenerated("Ticket ID is not generated for the ticket.");
//        }
//
//        String qrData= Base64Encoding.decode(oldQRData);
//        String[] qrDataArray=qrData.split(destination);
//
//        String fareFormatted = String.format("%04d", fare);
//        String sourceFormatted = source.replace("st", "");
//        String destinationFormatted = destination.replace("st", "");
//
//        // 9 Parameters are used to generate the QR code
//        String qrData = lineNumber + delimiter + // 6 digit 0
//                ticketId + delimiter + // 6 digit   1
//                issuanceTime + delimiter + // 10 digit  2
//                validityTime + delimiter + // 10 digit  3
//                fareFormatted + delimiter + // 4 digit  4
//                sourceFormatted + delimiter + // 2 digit    5
//                destinationFormatted + delimiter + // 2 digit   6
//                String.format("%02d", ticketTypeId) + delimiter + // 2 digit    7
//                String.format("%02d", ticketQuantity)+delimiter+ // 2 digit     8
//                new String("0")+delimiter;  //9
//
//        org.tinylog.Logger.info("QR Data: {}", qrData);
        return Base64Encoding.encode(oldQRData);
    }


}
