package com.amay.tom.grpc.scugrpc;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.QRTicket;

import com.amay.tom.model.Ticket;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.adjust.AdjustedTicket;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.tickets.PostGeneratedTicket;
import com.amay.tom.model.tickets.ProperTicket;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.StationData;
import com.amay.tom.utils.time.TimeUtil;
import org.amaytechnosystems.*;
import org.tinylog.Logger;
import org.transaction.qr.TransactionDetails;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.*;

public class ScuDataMapper {
    public static TicketRequestV1 createRequest() {
        return test();

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

        // Build the nested TicketDataV1 message
        ADevice device = ADevice.newBuilder()
                .setDeviceId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setDeviceType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(7))
                .setDeviceSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();

       AOperator operator = org.amaytechnosystems.AOperator.newBuilder()
                .setOperatorId(SystemConfig.getInstance().getCurrentUser().getUserId())
                .setShiftId(SystemConfig.getInstance().getLineNumber())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        AVersion versions = AVersion.newBuilder()
                .setSoftwareVer("1.2.3")
                .setTicketVer("2.1")
                .setFaretableVer("2024A")
                .build();

       ATicket ticket = ATicket.newBuilder()
                .setTicketNumber(qrTicket.getTicketNo())
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
                .setTicketType(TicketType.valueOf(qrTicket.getType()).getTicketTypeId())
                .setQuantity(qrTicket.getQty())
                .setPaymentMode(qrTicket.getFareMode())
                .setQrData(qrTicket.getQrCodeData())
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setDevice(device)
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

        // Build the nested TicketDataV1 message
        ADevice device = ADevice.newBuilder()
                .setDeviceId("DEV456")
                .setDeviceType("POS")
                .setDeviceSeq("SEQ789")
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
                .setSoftwareVer("1.2.3")
                .setTicketVer("2.1")
                .setFaretableVer("2024A")
                .build();

        ATicket ticket = ATicket.newBuilder()
                .setTicketNumber("TICKET" + UUID.randomUUID())
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
                .setTicketType("Single")
                .setQuantity(1)
                .setPaymentMode("CreditCard")
                .setQrData("QR" + UUID.randomUUID())
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setDevice(device)
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
        ADevice aDevice = ADevice.newBuilder()
                .setDeviceId(deviceId)
                .setDeviceType(deviceType)
                .setDeviceSeq(deviceSerial)
                .build();
        Version version= Version.newBuilder()
                .setDevice(aDevice)
                .build();

        return org.amaytechnosystems.VersionRequestV1.newBuilder()
                .setVersion(version)
                .build();
    }


    public static ShiftRequestV1 createShiftRequest(Shift shift) {

        shiftId=shift.getShiftId();
        operatorId=shift.getOperatorId();

        ADevice device = ADevice.newBuilder()
                .setDeviceId(shift.getDeviceId())
                .setDeviceType(shift.getDeviceId().substring(4,6))
                .setDeviceSeq(shift.getDeviceSerial())
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
                .setShiftStart(TimeUtil.localDateTimeToTimestamp(shift.getStartTime()))
                .setCurrentStatus(ShiftStatus.ACTIVE)
                .build();

        ShiftDataV1 shiftData = ShiftDataV1.newBuilder()
                .setDevice(device)
                .setOperator(operator)
                .setStation(station)
                .setShift(aShift)
                .build();

        return ShiftRequestV1.newBuilder()
                .setShiftData(shiftData)
                .build();
    }

    public static ShiftRequestV1 createShiftEndRequest(Shift shift) {
        ADevice device = ADevice.newBuilder()
                .setDeviceId(shift.getDeviceId())
                .setDeviceType(shift.getDeviceId().substring(4,6))
                .setDeviceSeq(shift.getDeviceSerial())
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
                .setShiftEnd(TimeUtil.localDateTimeToTimestamp(shift.getEndTime()))
                .setCurrentStatus(ShiftStatus.COMPLETED)
                .build();

        ShiftDataV1 shiftData = ShiftDataV1.newBuilder()
                .setDevice(device)
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

        ADevice device = ADevice.newBuilder()
                .setDeviceId(shift.getDeviceId())
                .setDeviceType(shift.getDeviceId().substring(4,6))
                .setDeviceSeq(shift.getDeviceSerial())
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
                .setDevice(device)
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
        ADevice device = ADevice.newBuilder()
                .setDeviceId(shift.getDeviceId())
                .setDeviceType(shift.getDeviceId().substring(4,6))
                .setDeviceSeq(shift.getDeviceSerial())
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
                .setDevice(device)
                .setOperator(operator)
                .setStation(station)
                .setShift(aShift)
                .build();

        return ShiftRequestV1.newBuilder()
                .setShiftData(shiftData)
                .build();
    }

    public static TicketRequestV1 createTicketIssueRequest(String orderId, String transactionId, PostGeneratedTicket postGeneratedTicket, Shift shift) {

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
        ADevice device = ADevice.newBuilder()
                .setDeviceId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setDeviceType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setDeviceSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
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
                .setSoftwareVer("1.2.3")
                .setTicketVer("2.1")
                .setFaretableVer("2024A")
                .build();

        ATicket ticket = ATicket.newBuilder()
                .setTicketNumber(postGeneratedTicket.getTicketId())
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
                .setTicketType( postGeneratedTicket.getProperTicket().getTicketType().getTicketTypeId())
                .setQuantity( postGeneratedTicket.getProperTicket().getQuantity())
                .setPaymentMode("Cash")
                .setQrData( postGeneratedTicket.getQrCodeString())
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setDevice(device)
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
        ADevice device = ADevice.newBuilder()
                .setDeviceId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setDeviceType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setDeviceSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();

        AOperator operator = org.amaytechnosystems.AOperator.newBuilder()
                .setOperatorId(shiftId)
                .setShiftId(operatorId)
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        AVersion versions = AVersion.newBuilder()
                .setSoftwareVer("1.2.3")
                .setTicketVer("2.1")
                .setFaretableVer("2024A")
                .build();

        ATicket ticket = ATicket.newBuilder()
                .setTicketNumber(ticketNumber)
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setDevice(device)
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


    public static TicketAdjustedRequestV1 createTicketAdjustRequest(String orderId, String transactionId, AdjustedTicket adjustedTicket, Shift shift) {
        // Build the nested TicketDataV1 message
        ADevice device = ADevice.newBuilder()
                .setDeviceId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setDeviceType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setDeviceSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
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
                .setTicketNumber(adjustedTicket.getTicketNumber())
//                .setReason(adjustedTicket.getReason())
                .setCreatedAt(TimeUtil.localDateTimeToTimestamp(adjustedTicket.getCreatedAt()))
                .setUpdatedAt(TimeUtil.localDateTimeToTimestamp(adjustedTicket.getUpdatedAt()))
                .setArea(adjustmentArea.valueOf(adjustedTicket.getArea()))
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
                .setDevice(device)
                .build();

        TicketAdjustedRequestV1 ticketAdjustedRequestV1=TicketAdjustedRequestV1.newBuilder()
                .setTicketData(ticketAdjustedDataV1)
                .build();

        return ticketAdjustedRequestV1;


    }

    public static TomStockRequestV1 getStockRequest(String shiftId, String equipmentId, int ncmcTotal, int qrTotal) {
        ADevice device = ADevice.newBuilder()
                .setDeviceId(equipmentId)
                .setDeviceType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setDeviceSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(7))
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
                .setDevice(device)
                .setOperator(operator)
                .setStock(stock)
                .build();

        TomStockRequestV1 tomStockRequestV1=TomStockRequestV1.newBuilder()
                .setStockData(tomStockDataV1)
                .build();

        return tomStockRequestV1;


    }

    public static TomStockRequestV1  getStockSoldRequest(String shiftId, String equipmentId, int qrSold, int ncmcSold) {
        ADevice device = ADevice.newBuilder()
                .setDeviceId(equipmentId)
                .setDeviceType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setDeviceSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(7))
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
                .setDevice(device)
                .setOperator(operator)
                .setStock(stock)
                .build();

        TomStockRequestV1 tomStockRequestV1=TomStockRequestV1.newBuilder()
                .setStockData(tomStockDataV1)
                .build();

        return tomStockRequestV1;
    }

    public static TicketRefundRequestV1 createTicketRefundRequestByNumber(String  ticketNumber, String refundMode) {

        // Build the nested TicketDataV1 message
        ADevice device = ADevice.newBuilder()
                .setDeviceId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setDeviceType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setDeviceSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();

        AOperator operator = AOperator.newBuilder()
//                .setOperatorId(shift.getOperatorId())
//                .setShiftId(shift.getShiftId())
                .build();

        AStation station = AStation.newBuilder()
                .setLineId(SystemConfig.getInstance().getLineNumber())
                .setStationId(SystemConfig.getInstance().getCurrentStation().getStationId())
                .build();

        ARefundTicket aRefundTicket= ARefundTicket.newBuilder()
                .setTicketNumber(ticketNumber)
                .setRefundMode(refundMode)
                .build();

        TicketRefundDataV1 ticketAdjustedDataV1=TicketRefundDataV1.newBuilder()
                .setRefundInfo(aRefundTicket)
                .setStation(station)
                .setOperator(operator)
                .setDevice(device)
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
        ADevice device = ADevice.newBuilder()
                .setDeviceId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setDeviceType(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId().substring(4,6))
                .setDeviceSeq(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
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
//                .setSoftwareVer(ticket.getSoftwareVer())
//                .setTicketVer(ticket.getTicketVer())
//                .setFaretableVer(ticket.getFaretableVer())
                .build();

        ATicket ticketProto = ATicket.newBuilder()
                .setTicketNumber( ticket.getTicketId())
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
                .setTicketType(  ticket.getTicketType())
                .setQuantity(  ticket.getQuantity())
                .setPaymentMode(ticket.getPaymentMode())
                .setQrData(  ticket.getQrData())
                .build();

        TicketDataV1 ticketData = TicketDataV1.newBuilder()
                .setDevice(device)
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
}
