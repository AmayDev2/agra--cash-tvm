package com.amay.tom.grpc.scugrpc;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.PayMethod;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.adjust.AdjustedTicket;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.tickets.PostGeneratedTicket;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.model.version.MasterConfigInfo;
import com.amay.tom.utils.time.TimeUtil;
import com.google.protobuf.util.Timestamps;
import org.amaytechnosystems.*;
import org.tinylog.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.amaytechnosystems.AdjustmentType.ADJUST_ENTRY;
import static org.amaytechnosystems.AdjustmentType.ADJUST_EXIT;

public class ScuDataMapper {
    private static MasterConfigInfo masterConfigInfo;
    
    public static TicketRequestV1 createRequest() {
        return test();

    }
    
    public static void setVersion(MasterConfigInfo masterConfigInfo){
        ScuDataMapper.masterConfigInfo=masterConfigInfo;
    }

    public static void removeVersion(MasterConfigInfo masterConfigInfo){
        ScuDataMapper.masterConfigInfo=null;
    }
    
    @Deprecated
    public static TicketRequestV1 createRequest(QRTicket qrTicket, String orderId) {
        String ipAddress = "Unknown";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace(); // Handle the exception as needed
        }

        RequestMetaData requestMetaData = RequestMetaData.newBuilder()
                .setRequestId(String.valueOf(UUID.randomUUID()))
                .setRequestIp(ipAddress)
                .setRequestSource(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setRequestTime(new Date().toString())
                .setRequestType("purchase")
                .build();

        AEquipment aEquipment = AEquipment.newBuilder()
                .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(7))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();

        AOperator operator = AOperator.newBuilder()
                .setOperatorId(SystemConfig.getInstance().getCurrentUser().getUserId())
                .setShiftId(SystemConfig.getInstance().getLineNumber())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        AVersion versions = AVersion.newBuilder()
                .setSoftwareVer(masterConfigInfo.getTomSwVer())
                .setTicketVer(masterConfigInfo.getProductConfig())
                .setFaretableVer(masterConfigInfo.getFareConfig())
                .build();

        ATicket ticket = ATicket.newBuilder()
                .setTicketId(qrTicket.getTicketNo())
                .setOrderId(orderId)
                .setTransactionId("NULL")
                .setTransactionUk(qrTicket.getTicketNo() + qrTicket.getInitiateDateTime())
                .setTicketIssue(qrTicket.getInitiateDateTime())
                .setTicketExp(qrTicket.getExpiryTime())
                .setSourceStation(qrTicket.getFrom())
                .setDestinationStation(qrTicket.getTo())
                .setLanguage("en")
                .setAmount(Double.parseDouble(qrTicket.getPrice()))
                .setDiscount(2.00)
                .setProductId(qrTicket.getType())
                .setQuantity(qrTicket.getQty())
                .setPaymentMode(qrTicket.getFareMode())
                .setQrData(qrTicket.getQrCodeData())
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setEquipment(aEquipment)
                .setOperator(operator)
                .setStation(station)
                .setVersions(versions)
                .setTicket(ticket)
                .build();

        TicketRequestV1 purchaseRequest = TicketRequestV1.newBuilder()
                .setRequestMetaData(requestMetaData)
                .setTicketData(ticketData)
                .build();

        return purchaseRequest;
    }


    static TicketRequestV1 test() {
        RequestMetaData requestMetaData = RequestMetaData.newBuilder()
                .setRequestId(String.valueOf(UUID.randomUUID()))
                .setRequestIp("192.168.1.1")
                .setRequestSource("MobileApp")
                .setRequestTime("2024-06-27T12:34:56Z")
                .setRequestType("purchase")
                .build();

        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId("DEV456")
                .setEquipmentType("POS")
                .setEquipmentSeq("SEQ789")
                .build();

        AOperator operator = AOperator.newBuilder()
                .setOperatorId("OP123")
                .setShiftId("SHIFT01")
                .build();

        AStation station = AStation.newBuilder()
                .setLineId("LINE1")
                .setStationId("STN1001")
                .build();

        AVersion versions = AVersion.newBuilder()
                .setSoftwareVer(masterConfigInfo.getTomSwVer())
                .setTicketVer(masterConfigInfo.getProductConfig())
                .setFaretableVer(masterConfigInfo.getFareConfig())
                .build();

        ATicket ticket = ATicket.newBuilder()
                .setTicketId("TICKET" + UUID.randomUUID())
                .setOrderId(String.valueOf(UUID.randomUUID()))
                .setTransactionId(String.valueOf(UUID.randomUUID().clockSequence()))
                .setTransactionUk("UKTXN12345")
                .setTicketIssue(String.valueOf(Instant.now().toEpochMilli()))
                .setTicketExp(String.valueOf(Instant.now().toEpochMilli() + 30 * 60 * 1000))
                .setSourceStation("Central Station")
                .setDestinationStation("North Station")
                .setLanguage("en")
                .setAmount(15.50)
                .setDiscount(2.00)
                .setTicketId("Single")
                .setQuantity(1)
                .setPaymentMode("CreditCard")
                .setQrData("QR" + UUID.randomUUID())
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStation(station)
                .setVersions(versions)
                .setTicket(ticket)
                .build();

        TicketRequestV1 purchaseRequest = TicketRequestV1.newBuilder()
                .setRequestMetaData(requestMetaData)
                .setTicketData(ticketData)
                .build();

        return purchaseRequest;
    }


    @Deprecated
    public static TicketRequestV1[] createRequestList(List<QRTicket> qrTickets, String orderId) {
        List<TicketRequestV1> list=new ArrayList<>();
        for(QRTicket qrTicket:qrTickets){
           list.add( createRequest(qrTicket,orderId));
        }
        return list.toArray(TicketRequestV1[]::new);
    }

    @Deprecated
    public static org.amaytechnosystems.VersionRequestV1 createVersionRequest(String deviceType, String deviceId, String deviceSerial) {
        AEquipment aEquipment = AEquipment.newBuilder()
                .setEquipmentId(deviceId)
                .setEquipmentType(deviceType)
                .setEquipmentSeq(deviceSerial)
                .build();
        Version version= Version.newBuilder()
                .setEquipment(aEquipment)
                .build();

        return org.amaytechnosystems.VersionRequestV1.newBuilder()
                .setVersion(version)
                .build();
    }


    public static ShiftRequestV1 createShiftRequest(Shift shift) {
        shiftId=shift.getShiftId();
        operatorId=shift.getOperatorId();

        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(shift.getDeviceId())
                .setEquipmentType(shift.getDeviceId().substring(4,6))
                .setEquipmentSeq(shift.getDeviceId().substring(7))
                .build();

        AOperator operator = AOperator.newBuilder()
                .setOperatorId(shift.getOperatorId())
                .setShiftId(shift.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(shift.getLineNo())
                .setStationId(shift.getStationId())
                .build();

        AShift.Builder aShift = AShift.newBuilder()
                .setShiftId(shift.getShiftId())
                .setShiftStart(TimeUtil.localDateTimeToTimestamp(shift.getStartTime()))
                .setCurrentStatus(ShiftStatus.valueOf(shift.getCurrentStatus()));

        ShiftDataV1 shiftData = ShiftDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStation(station)
                .setShift(aShift.build())
                .build();

        return ShiftRequestV1.newBuilder()
                .setShiftData(shiftData)
                .build();
    }

    public static ShiftRequestV1 createShiftEndRequest(Shift shift) {
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(shift.getDeviceId())
                .setEquipmentType(shift.getDeviceId().substring(4,6))
                .setEquipmentSeq(shift.getDeviceSerial())
                .build();

        AOperator operator = AOperator.newBuilder()
                .setOperatorId(shift.getOperatorId())
                .setShiftId(shift.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(shift.getLineNo())
                .setStationId(shift.getStationId())
                .build();

        AShift aShift = AShift.newBuilder()
                .setShiftId(shift.getShiftId())
//                .setShiftStart(TimeUtil.localDateTimeToTimestamp(shift.getStartTime()))
                .setShiftEnd(TimeUtil.localDateTimeToTimestamp(shift.getEndTime()))
                .setCurrentStatus(ShiftStatus.COMPLETED)
//                .setImprestMoney(Integer.parseInt(shift.getImprest_money()))
                .setVersion(shift.getConfig_version())
                .build();

        ShiftDataV1 shiftData = ShiftDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStation(station)
                .setShift(aShift)
                .build();

        return ShiftRequestV1.newBuilder()
                .setShiftData(shiftData)
                .build();

    }

    public static ShiftRequestV1 createShiftPauseRequest(Shift shift) {
        shiftId=null;
        operatorId=null;

        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(shift.getDeviceId())
                .setEquipmentType(shift.getDeviceId().substring(4,6))
                .setEquipmentSeq(shift.getDeviceSerial())
                .build();

        AOperator operator = AOperator.newBuilder()
                .setOperatorId(shift.getOperatorId())
                .setShiftId(shift.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(shift.getLineNo())
                .setStationId(shift.getStationId())
                .build();

        AShift aShift = AShift.newBuilder()
                .setShiftId(shift.getShiftId())
                .setCurrentStatus(ShiftStatus.PAUSED)
                .build();

        ShiftDataV1 shiftData = ShiftDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStation(station)
                .setShift(aShift)
                .build();

        return ShiftRequestV1.newBuilder()
                .setShiftData(shiftData)
                .build();
    }

    private static String shiftId;
    private static String operatorId;

    public static ShiftRequestV1 createShiftResumeRequest(Shift shift) {
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(shift.getDeviceId())
                .setEquipmentType(shift.getDeviceId().substring(4,6))
                .setEquipmentSeq(shift.getDeviceSerial())
                .build();

        AOperator operator = AOperator.newBuilder()
                .setOperatorId(shift.getOperatorId())
                .setShiftId(shift.getShiftId())
                .build();


        shiftId=shift.getShiftId();
        operatorId=shift.getOperatorId();

        AStation station = AStation.newBuilder()
                .setLineId(shift.getLineNo())
                .setStationId(shift.getStationId())
                .build();

        AShift aShift = AShift.newBuilder()
                .setShiftId(shift.getShiftId())
                .setCurrentStatus(ShiftStatus.ACTIVE)
                .build();

        ShiftDataV1 shiftData = ShiftDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStation(station)
                .setShift(aShift)
                .build();

        return ShiftRequestV1.newBuilder()
                .setShiftData(shiftData)
                .build();
    }

    public static ShiftRequestV1 createShiftPushRequest(Shift shift) {
        shiftId=shift.getShiftId();
        operatorId=shift.getOperatorId();

        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(shift.getDeviceId())
                .setEquipmentType(shift.getDeviceId().substring(4,6))
                .setEquipmentSeq(shift.getDeviceId().substring(6))
                .build();

        AOperator operator = AOperator.newBuilder()
                .setOperatorId(shift.getOperatorId())
                .setShiftId(shift.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(shift.getLineNo())
                .setStationId(shift.getStationId())
                .build();

        AShift.Builder aShift = AShift.newBuilder()
                .setShiftId(shift.getShiftId())
                .setShiftStart(TimeUtil.localDateTimeToTimestamp(shift.getStartTime()))
                .setCurrentStatus(ShiftStatus.valueOf(shift.getCurrentStatus()));

        if(shift.getEndTime()!=null){
            aShift.setShiftEnd(TimeUtil.localDateTimeToTimestamp(shift.getEndTime()));
        }

        ShiftDataV1 shiftData = ShiftDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStation(station)
                .setShift(aShift.build())
                .build();

        return ShiftRequestV1.newBuilder()
                .setShiftData(shiftData)
                .build();
    }

    public static TicketRequestV1 createTicketIssueRequest(String orderId, String transactionId, PostGeneratedTicket postGeneratedTicket, Shift shift, PaymentResponse paymentResponse) {

        String ipAddress = "Unknown";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace(); // Handle the exception as needed
        }

        RequestMetaData requestMetaData = RequestMetaData.newBuilder()
                .setRequestId(String.valueOf(UUID.randomUUID()))
                .setRequestIp(ipAddress)
                .setRequestSource(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setRequestTime(new Date().toString())
                .setRequestType("purchase")
                .build();

        // Build the nested TicketDataV1 message
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();

        AOperator operator = org.amaytechnosystems.AOperator.newBuilder()
                .setOperatorId(shift.getOperatorId())
                .setShiftId(shift.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        AVersion versions = AVersion.newBuilder()
                .setSoftwareVer(masterConfigInfo.getTomSwVer())
                .setTicketVer(masterConfigInfo.getProductConfig())
                .setFaretableVer(masterConfigInfo.getFareConfig())
                .build();
        ATicket ticket = ATicket.newBuilder()
                .setTicketId(postGeneratedTicket.getTicketId())
                .setOrderId(orderId)
                .setTransactionId(transactionId)
                .setTransactionUk(postGeneratedTicket.getTicketId() + postGeneratedTicket.getProperTicket().getIssuedAt())
                .setTicketIssue(String.valueOf(postGeneratedTicket.getProperTicket().getIssuedAt()))
                .setTicketExp(String.valueOf(postGeneratedTicket.getProperTicket().getValidUntil()))
                .setSourceStation( postGeneratedTicket.getProperTicket().getSource().getStationId())
                .setDestinationStation( postGeneratedTicket.getProperTicket().getDestination().getStationId())
                .setLanguage("en")
                .setAmount(postGeneratedTicket.getProperTicket().getPrice())
                .setDiscount(0)
                .setProductId( postGeneratedTicket.getProperTicket().getTicketType().getTicketTypeId())
                .setQuantity( postGeneratedTicket.getProperTicket().getQuantity())
                .setPaymentMode(paymentResponse.getPaymentMode())
                .setQrData( postGeneratedTicket.getQrCodeString())
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStation(station)
                .setVersions(versions)
                .setTicket(ticket)
                .build();

        TicketRequestV1 purchaseRequest = TicketRequestV1.newBuilder()
                .setRequestMetaData(requestMetaData)
                .setTicketData(ticketData)
                .build();

        return purchaseRequest;
    }


    public static TicketRequestV1 createTicketInfoRequestByNumber(String ticketNumber) {

        String ipAddress = "Unknown";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace(); // Handle the exception as needed
        }

        RequestMetaData requestMetaData = RequestMetaData.newBuilder()
                .setRequestId(String.valueOf(UUID.randomUUID()))
                .setRequestIp(ipAddress)
                .setRequestSource(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setRequestTime(new Date().toString())
                .setRequestType("purchase")
                .build();

        // Build the nested TicketDataV1 message
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();

        AOperator operator = org.amaytechnosystems.AOperator.newBuilder()
                .setOperatorId(operatorId)
                .setShiftId(shiftId)
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        AVersion versions = AVersion.newBuilder()
                .setSoftwareVer(masterConfigInfo.getTomSwVer())
                .setTicketVer(masterConfigInfo.getProductConfig())
                .setFaretableVer(masterConfigInfo.getFareConfig())
                .build();

        ATicket ticket = ATicket.newBuilder()
                .setTicketId(ticketNumber)
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStation(station)
                .setVersions(versions)
                .setTicket(ticket)
                .build();

        TicketRequestV1 infoRequest = TicketRequestV1.newBuilder()
                .setRequestMetaData(requestMetaData)
                .setTicketData(ticketData)

                .build();

        return infoRequest;
    }

    public static TicketAdjustedRequestV1 createAdjustTicketPushRequest(String orderId, String transactionId, AdjustedTicket adjustedTicket) {
        List<AdjustmentType> adjustmentTypes = new ArrayList<>();
        for (String adjustmentType : adjustedTicket.getAdjustmentType().replaceAll("\\s+", "").split(",")) {
            switch (com.amay.tom.enums.AdjustmentType.valueOf(adjustmentType)) {
                case ADJUST_ENTRY:
                    adjustmentTypes.add(ADJUST_ENTRY);
                    break;
                case ADJUST_EXIT:
                    adjustmentTypes.add(ADJUST_EXIT);
                    break;
                case OVER_STAY:
                    adjustmentTypes.add(AdjustmentType.OVERTIME_OVERRIDE);
                    break;
                case OVER_TRAVEL:
                    adjustmentTypes.add(AdjustmentType.OVERTRAVEL_OVERRIDE);
                    break;
            }
        }
        // Build the nested TicketDataV1 message
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();
        AOperator operator = AOperator.newBuilder()
                .setOperatorId(adjustedTicket.getOperatorId())
                .setShiftId(adjustedTicket.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        org.amaytechnosystems.AdjustedTicket gRPCAdjustedTicket=org.amaytechnosystems.AdjustedTicket.newBuilder()
                .setOrderId(orderId)
//                .setAdjustmentType(AdjustmentType.valueOf(adjustedTicket.getAdjustmentType().toUpperCase()))
                .setEncryptedQR(adjustedTicket.getEncryptedQR())
//                .setIssueTime(adjustedTicket.getIssueTime())
                .setAdjustId(adjustedTicket.getAdjustId())
//                .setEntryTime(adjustedTicket.getEntryTime())
//                .setExitTime(adjustedTicket.getExitTime())
//                .setDestination(adjustedTicket.getDestination())
                .setTicketId(adjustedTicket.getTicketNumber())
//                .setReason(adjustedTicket.getReason())
                .setCreatedAt(TimeUtil.localDateTimeToTimestamp(adjustedTicket.getCreatedAt()))
                .setUpdatedAt(TimeUtil.localDateTimeToTimestamp(adjustedTicket.getUpdatedAt()))
                .setArea(AdjustmentArea.valueOf(adjustedTicket.getArea()))
                //transaction time
                .build();

        AdjustmentDetail.Builder adjustmentDetail = AdjustmentDetail.newBuilder()
                .setAdjustId(adjustedTicket.getAdjustId())
                .setTicketId(adjustedTicket.getTicketNumber())
                .setReason("Adjust Reason")
                .setAmount(Integer.parseInt(adjustedTicket.getPenaltyAmount()))
                .setAdjustmentArea(adjustedTicket.getArea().equalsIgnoreCase("UNPAID")?AdjustmentArea.UNPAID:AdjustmentArea.PAID)
                .setShiftId(adjustedTicket.getShiftId())
                .setOperatorId(adjustedTicket.getOperatorId())
                .addAllAdjustmentType(adjustmentTypes);


        if(adjustedTicket.getTransactionTime()==null){
            adjustmentDetail.setAdjustmentTime(String.valueOf(Instant.now().toEpochMilli()));
        }else adjustmentDetail.setAdjustmentTime(adjustedTicket.getTransactionTime().toString());

        ATransaction.Builder transactionDetails=ATransaction.newBuilder()
                .setOrderId(orderId)
                .setTransactionId(transactionId)
                .setTransactionAmount(adjustedTicket.getPenaltyAmount())
                .setTransactionMode(adjustedTicket.getPaymentMode());

        if(adjustedTicket.getTransactionTime()==null){
            transactionDetails.setTransactionTime(String.valueOf(Instant.now().toEpochMilli())).build();
        }else transactionDetails.setTransactionTime(adjustedTicket.getTransactionTime().toString()).build();

        TicketAdjustedDataV1 ticketAdjustedDataV1=TicketAdjustedDataV1.newBuilder()
                .setStation(station)
                .setTransaction(transactionDetails)
                .setAdjustedTicket(gRPCAdjustedTicket)
                .setOperator(operator)
                .setEquipment(equipment)
                .setDetails(adjustmentDetail)
                .build();

        TicketAdjustedRequestV1 ticketAdjustedRequestV1=TicketAdjustedRequestV1.newBuilder()
                .setTicketData(ticketAdjustedDataV1)
                .build();

        return ticketAdjustedRequestV1;
    }



    public static TicketAdjustedRequestV1 createTicketAdjustRequest(String orderId, String transactionId, AdjustedTicket adjustedTicket, Shift shift) {
        List<AdjustmentType> adjustmentTypes= new ArrayList<>();
        for(com.amay.tom.enums.AdjustmentType adjustmentType:adjustedTicket.getTicketInfo().getAdjustmentType()){
            switch (adjustmentType){
                case ADJUST_ENTRY:
                    adjustmentTypes.add(ADJUST_ENTRY);
                    break;
                case ADJUST_EXIT:
                    adjustmentTypes.add(ADJUST_EXIT);
                    break;
                case OVER_STAY:
                    adjustmentTypes.add(AdjustmentType.OVERTIME_OVERRIDE);
                    break;
                case OVER_TRAVEL:
                    adjustmentTypes.add(AdjustmentType.OVERTRAVEL_OVERRIDE);
                    break;
            }
        }


        // Build the nested TicketDataV1 message
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();
        AOperator operator = AOperator.newBuilder()
                .setOperatorId(shift.getOperatorId())
                .setShiftId(shift.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        org.amaytechnosystems.AdjustedTicket gRPCAdjustedTicket=org.amaytechnosystems.AdjustedTicket.newBuilder()
                .setOrderId(orderId)
//                .setAdjustmentType(AdjustmentType.valueOf(adjustedTicket.getAdjustmentType().toUpperCase()))
                .setEncryptedQR(adjustedTicket.getEncryptedQR())
//                .setIssueTime(adjustedTicket.getIssueTime())
                .setAdjustId(adjustedTicket.getAdjustId())
//                .setEntryTime(adjustedTicket.getEntryTime())
//                .setExitTime(adjustedTicket.getExitTime())
//                .setDestination(adjustedTicket.getDestination())
                .setTicketId(adjustedTicket.getTicketNumber())
//                .setReason(adjustedTicket.getReason())
                .setCreatedAt(TimeUtil.localDateTimeToTimestamp(adjustedTicket.getCreatedAt()))
                .setUpdatedAt(TimeUtil.localDateTimeToTimestamp(adjustedTicket.getUpdatedAt()))
                .setArea(AdjustmentArea.valueOf(adjustedTicket.getArea()))
                //transaction time
                .build();

        AdjustmentDetail adjustmentDetail = AdjustmentDetail.newBuilder()
                .setAdjustId(adjustedTicket.getAdjustId())
                .setTicketId(adjustedTicket.getTicketNumber())
                .setReason("Adjust Reason")
                .setAmount(Integer.parseInt(adjustedTicket.getPenaltyAmount()))
                .setAdjustmentArea(adjustedTicket.getArea().equalsIgnoreCase("UNPAID")?AdjustmentArea.UNPAID:AdjustmentArea.PAID)
                .setShiftId(shiftId)
                .setOperatorId(operatorId)
                .addAllAdjustmentType(adjustmentTypes)
                .setAdjustmentTime(String.valueOf(Instant.now().toEpochMilli())) // EPOCH MILI
                .build();

        ATransaction transactionDetails=ATransaction.newBuilder()
                .setOrderId(orderId)
                .setTransactionId(transactionId)
                .setTransactionAmount(adjustedTicket.getPenaltyAmount())
                .setTransactionMode(adjustedTicket.getPaymentMode())
                .setTransactionTime(String.valueOf(Instant.now().toEpochMilli()))
                .build();

        TicketAdjustedDataV1 ticketAdjustedDataV1=TicketAdjustedDataV1.newBuilder()
                .setStation(station)
                .setTransaction(transactionDetails)
                .setAdjustedTicket(gRPCAdjustedTicket)
                .setOperator(operator)
                .setEquipment(equipment)
                .setDetails(adjustmentDetail)
                .build();

        TicketAdjustedRequestV1 ticketAdjustedRequestV1=TicketAdjustedRequestV1.newBuilder()
                .setTicketData(ticketAdjustedDataV1)
                .build();

        return ticketAdjustedRequestV1;


    }


    @Deprecated
    public static TicketAdjustedRequestV1 createTicketAdjustRequest_DEP(String orderId, String transactionId, AdjustedTicket adjustedTicket, Shift shift) {
        // Build the nested TicketDataV1 message
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();
        AOperator operator = AOperator.newBuilder()
                .setOperatorId(shift.getOperatorId())
                .setShiftId(shift.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        org.amaytechnosystems.AdjustedTicket gRPCAdjustedTicket=org.amaytechnosystems.AdjustedTicket.newBuilder()
                .setOrderId(orderId)
                .setAdjustmentType(AdjustmentType.valueOf(adjustedTicket.getAdjustmentType().toUpperCase()))
                .setEncryptedQR(adjustedTicket.getEncryptedQR())
                .setIssueTime(adjustedTicket.getIssueTime())
                .setAdjustId(adjustedTicket.getAdjustId())
//                .setEntryTime(adjustedTicket.getEntryTime())
//                .setExitTime(adjustedTicket.getExitTime())
                .setDestination(adjustedTicket.getDestination())
                .setTicketId(adjustedTicket.getTicketNumber())
//                .setReason(adjustedTicket.getReason())
                .setCreatedAt(TimeUtil.localDateTimeToTimestamp(adjustedTicket.getCreatedAt()))
                .setUpdatedAt(TimeUtil.localDateTimeToTimestamp(adjustedTicket.getUpdatedAt()))
                .setArea(AdjustmentArea.valueOf(adjustedTicket.getArea()))
                //transaction time
                .build();

        ATransaction transactionDetails=ATransaction.newBuilder()
                .setOrderId(orderId)
                .setTransactionId(transactionId)
                .setTransactionAmount(adjustedTicket.getPenaltyAmount())
                .build();

        TicketAdjustedDataV1 ticketAdjustedDataV1=TicketAdjustedDataV1.newBuilder()
                .setAdjustedTicket(gRPCAdjustedTicket)
                .setStation(station)
                .setTransaction(transactionDetails)
                .setOperator(operator)
                .setEquipment(equipment)
                .build();

        TicketAdjustedRequestV1 ticketAdjustedRequestV1=TicketAdjustedRequestV1.newBuilder()
                .setTicketData(ticketAdjustedDataV1)
                .build();

        return ticketAdjustedRequestV1;


    }

    public static TomStockRequestV1 getStockRequest(String shiftId, String equipmentId, int ncmcTotal, int qrTotal) {
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(equipmentId)
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(7))
                .build();
        AOperator operator = AOperator.newBuilder()
                .setShiftId(shiftId)
                .build();
        AStock stock = AStock.newBuilder()
                .setQrStock(qrTotal)
                .setCscStock(ncmcTotal)
//                .setTime(TimeUtil.localDateTimeToTimestamp(LocalDateTime.from(Instant.now())))
                .build();

        TomStockDataV1 tomStockDataV1=TomStockDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStock(stock)
                .build();

        TomStockRequestV1 tomStockRequestV1=TomStockRequestV1.newBuilder()
                .setStockData(tomStockDataV1)
                .build();

        return tomStockRequestV1;


    }

    public static TomStockRequestV1  getStockSoldRequest(String shiftId, String equipmentId, int qrSold, int ncmcSold) {
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(equipmentId)
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(7))
                .build();
        AOperator operator = AOperator.newBuilder()
                .setShiftId(shiftId)
                .build();
        AStock stock = AStock.newBuilder()
                .setQrSales(qrSold)
                .setCscSales(ncmcSold)
//                .setTime(TimeUtil.localDateTimeToTimestamp(LocalDateTime.from(Instant.now())))
                .build();

        TomStockDataV1 tomStockDataV1=TomStockDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStock(stock)
                .build();

        TomStockRequestV1 tomStockRequestV1=TomStockRequestV1.newBuilder()
                .setStockData(tomStockDataV1)
                .build();

        return tomStockRequestV1;
    }

    public static TicketRefundRequestV1 createTicketRefundRequestByNumber(String  ticketNumber, String refundMode, int refundAmount,String ticketType, String refundId) {

        // Build the nested TicketDataV1 message
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();

        AOperator operator = AOperator.newBuilder()
                .setOperatorId(operatorId)
                .setShiftId(shiftId)
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        ARefundTicket aRefundTicket= ARefundTicket.newBuilder()
                .setTicketId(ticketNumber)
                .setRefundMode(refundMode)
                .setRefundId(refundId)
                .setRefundAmount(String.valueOf(refundAmount))
                .setProductId(ticketType)
                .setTime(Timestamps.fromMillis(Instant.now().toEpochMilli()))
                .build();

        ATransaction aTransaction = ATransaction.newBuilder()
                .build();

        TicketRefundDataV1 ticketAdjustedDataV1=TicketRefundDataV1.newBuilder()
                .setRefundInfo(aRefundTicket)
                .setStation(station)
                .setOperator(operator)
                .setEquipment(equipment)
                .setTransactionInfo(aTransaction)
                .build();

        TicketRefundRequestV1 ticketAdjustedRequestV1=TicketRefundRequestV1.newBuilder()
                .setTicketRefundData(ticketAdjustedDataV1)
                .build();

        return ticketAdjustedRequestV1;




    }

    public static TicketRefundRequestV1 createTicketRefundPushRequestByNumber(String  ticketNumber, String refundMode, int refundAmount,String ticketType, String refundId, String operatorId, String shiftId) {

        // Build the nested TicketDataV1 message
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();

        AOperator operator = AOperator.newBuilder()
                .setOperatorId(operatorId)
                .setShiftId(shiftId)
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        ARefundTicket aRefundTicket= ARefundTicket.newBuilder()
                .setTicketId(ticketNumber)
                .setRefundMode(refundMode)
                .setRefundId(refundId)
                .setRefundAmount(String.valueOf(refundAmount))
                .setProductId(ticketType)
                .setTime(Timestamps.fromMillis(Instant.now().toEpochMilli()))
                .build();

        ATransaction aTransaction = ATransaction.newBuilder()
                .build();

        TicketRefundDataV1 ticketAdjustedDataV1=TicketRefundDataV1.newBuilder()
                .setRefundInfo(aRefundTicket)
                .setStation(station)
                .setOperator(operator)
                .setEquipment(equipment)
                .setTransactionInfo(aTransaction)
                .build();

        TicketRefundRequestV1 ticketAdjustedRequestV1=TicketRefundRequestV1.newBuilder()
                .setTicketRefundData(ticketAdjustedDataV1)
                .build();

        return ticketAdjustedRequestV1;




    }

    public static TicketRequestV1 createTicketIssueRequest(TicketsDto ticket) {

        String ipAddress = "Unknown";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            Logger.debug("Error getting local host address: {}", e.getMessage());
        }

        RequestMetaData requestMetaData = RequestMetaData.newBuilder()
                .setRequestId(String.valueOf(UUID.randomUUID()))
                .setRequestIp(ipAddress)
                .setRequestSource(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setRequestTime(new Date().toString())
                .setRequestType("purchase")
                .build();

        // Build the nested TicketDataV1 message
        AEquipment equipment = AEquipment.newBuilder()
                .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();

        AOperator operator = org.amaytechnosystems.AOperator.newBuilder()
                .setOperatorId(ticket.getOperatorId())
                .setShiftId(ticket.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        AVersion versions = AVersion.newBuilder()
                .setSoftwareVer(ticket.getSoftwareVer())
                .setTicketVer(ticket.getTicketVer())
                .setFaretableVer(ticket.getFaretableVer())
                .build();

        ATicket ticketProto = ATicket.newBuilder()
                .setTicketId( ticket.getTicketId())
                .setOrderId( ticket.getOrderId())
                .setTransactionId( ticket.getTransactionId())
                .setTransactionUk( ticket.getTicketId()+  ticket.getIssueAt())
                .setTicketIssue(String.valueOf(ticket.getIssueAt()))
                .setTicketExp(String.valueOf(ticket.getValidUntil()))
                .setSourceStation(  ticket.getInStation())
                .setDestinationStation(  ticket.getOutStation())
                .setLanguage("en")
                .setAmount( ticket.getAmount())
                .setDiscount(ticket.getDiscount())
                .setQuantity(  ticket.getQuantity())
                .setPaymentMode(ticket.getPaymentMode())
                .setQrData(  ticket.getQrData())
                .setProductId(ticket.getTicketType())
                .setIsActive(ticket.isActive())
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setEquipment(equipment)
                .setOperator(operator)
                .setStation(station)
                .setVersions(versions)
                .setTicket(ticketProto)
                .build();

        return TicketRequestV1.newBuilder()
                .setRequestMetaData(requestMetaData)
                .setTicketData(ticketData)
                .build();

    }

    public static LastShiftRequestV1 createLastShiftRequest() {
        return LastShiftRequestV1.newBuilder()
                .setStation(AStation.newBuilder()
                        .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                        .setLineId(SystemConfig.getInstance().getLineNumber())
                        .build())
                .setEquipment(AEquipment.newBuilder()
                        .setEquipmentSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                        .setEquipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId()))
               .build();
}
}
