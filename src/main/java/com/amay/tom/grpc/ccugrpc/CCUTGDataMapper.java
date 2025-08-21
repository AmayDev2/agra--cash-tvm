package com.amay.tom.grpc.ccugrpc;

import com.amay.tom.model.MetroTicket;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.tickets.ProperTicket;
import org.tinylog.Logger;
import org.transaction.qr.IssueTicket;
import org.transaction.qr.OperatorData;
import org.transaction.qr.TicketBaseData;

import java.time.Instant;

public class CCUTGDataMapper {

    public static IssueTicket getIssueTicketRequest(MetroTicket metroTicket, String orderId){

        String currentTime = String.valueOf(Instant.now().getEpochSecond());
        Logger.info("Current Time: {}",currentTime);
        // Create an OperatorData object
        OperatorData operatorData = OperatorData.newBuilder()
                .setLineId("3")
                .setStationId("345")
                .setEquipId("03021501")
                .setOprId("123")
                .setShiftId("234")

//                .setLineId(3) // Set the line ID
//                .setStationId(Integer.parseInt(SystemConfig.getInstance().getCurrentStation().getStationId().split("st")[1])) //TODO:  check datatype
//                .setEquipId(Integer.parseInt(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())) // Set the equipment ID
//                .setOprId(SystemConfig.getInstance().getCurrentUser().getUId()) // Set the operator ID
//                .setShiftId(32) // Set the shift ID
                .build(); // Build the OperatorData object
//
//// Create a TransactionData object
        TicketBaseData txnData = TicketBaseData.newBuilder()
                .setOrderId(orderId) // Set the order ID
//                .setPayType(1) // Set the payment type
                .setSrcId(Integer.parseInt(metroTicket.getSource().split("st")[1])) // Set the source ID
                .setDestId(Integer.parseInt(metroTicket.getDestination().split("st")[1])) // Set the destination ID
                .setTicketType(Integer.parseInt(metroTicket.getTicketType().getTicketTypeId())) // Set the ticket type
                .setIssueDatetime(currentTime) // Set the issue date/time
//                .setQrIssueMode(1) // Set the QR issue mode
                .setPassCount(metroTicket.getTicketQuantity()) // Set the number of passengers
                .setTicketFare(metroTicket.getFare()) // Set the ticket fare
//                .setTxnAmt(metroTicket.getFare()) // Set the transaction amount
//                .setDisAmt(0) // Set the discount amount
                .build(); // Build the TransactionData object

// Create a TicketRequestData object with OperatorData and TransactionData
        IssueTicket ticketRequest = IssueTicket.newBuilder()
                .setOperatorData(operatorData) // Set the OperatorData object
                .setBaseData(txnData) // Set the TransactionData object
                .build(); // Build the TicketRequestData object

        return ticketRequest;
    }

    public static IssueTicket getIssueTicketRequest(ProperTicket properTicket, Shift shift, String orderId) {
        Logger.debug("Getting Issue Ticket Request {}", properTicket);
        OperatorData operatorData = OperatorData.newBuilder()
                .setLineId(shift.getLineNo())
                .setStationId(shift.getStationId())
                .setEquipId(shift.getDeviceId())
                .setOprId(shift.getOperatorId())
                .setShiftId(shift.getShiftId())
                .build();

        TicketBaseData txnData = TicketBaseData.newBuilder()
                .setOrderId(orderId) // Set the order ID
                .setSrcId(Integer.parseInt(properTicket.getSource().getStationId().split("st")[1])) // Set the source ID
                .setDestId(Integer.parseInt(properTicket.getDestination().getStationId().split("st")[1])) // Set the destination ID
                .setTicketType(Integer.parseInt(properTicket.getTicketType().getTicketTypeId())) // Set the ticket type
                .setIssueDatetime(String.valueOf(properTicket.getIssuedAt())) // Set the issue date/time
                .setPassCount(properTicket.getQuantity()) // Set the number of passengers
                .build(); // Build the TransactionData object

        IssueTicket ticketRequest = IssueTicket.newBuilder()
                .setOperatorData(operatorData) // Set the OperatorData object
                .setBaseData(txnData) // Set the TransactionData object
                .build(); // Build the TicketRequestData object

        return ticketRequest;
    }
}
