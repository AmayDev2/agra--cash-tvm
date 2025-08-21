//package com.amay.tom.grpc;
//
//import com.google.protobuf.ByteString;
//import io.grpc.stub.StreamObserver;
//import org.junit.jupiter.api.Test;
//import org.ticket.tg.Ticketdata;
//import org.tinylog.Logger;
//import org.unitral.module.FileChunk;
//import org.unitral.module.FtpServiceGrpc;
//
//import java.io.*;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class GrpcServiceTestCase {
//
//
//    @Test
//    public void tgTest(){
//        GrpcServiceTest grpcService = new GrpcServiceTest();
//
//        String currentTime = String.valueOf(Instant.now().getEpochSecond());
//        Logger.info("Current Time: {}",currentTime);
//        // Create an OperatorData object
//        Ticketdata.OperatorData operatorData = Ticketdata.OperatorData.newBuilder()
//                .setLineId(03) // Set the line ID
//                .setStationId(02) // Set the station ID
//                .setEquipId(3) // Set the equipment ID
//                .setOprId(4) // Set the operator ID
//                .setShiftId(543) // Set the shift ID
//                .build(); // Build the OperatorData object
//
//// Create a TransactionData object
//        Ticketdata.TransactionData txnData = Ticketdata.TransactionData.newBuilder()
//                .setOrderId(String.valueOf(UUID.randomUUID())) // Set the order ID
//                .setPayType(1) // Set the payment type
//                .setSrcId(10) // Set the source ID
//                .setDestId(20) // Set the destination ID
//                .setTicketType(01) // Set the ticket type
//                .setIssueDateTime(currentTime) // Set the issue date/time
//                .setQrIssueMode(1) // Set the QR issue mode
//                .setPassCount(1) // Set the number of passengers
//                .setTicketFare(100) // Set the ticket fare
//                .setTxnAmt(150) // Set the transaction amount
//                .setDisAmt(10) // Set the discount amount
//                .build(); // Build the TransactionData object
//
//// Create a TicketRequestData object with OperatorData and TransactionData
//        Ticketdata.TicketRequestData ticketRequest = Ticketdata.TicketRequestData.newBuilder()
//                .setOperatorData(operatorData) // Set the OperatorData object
//                .setTxnData(txnData) // Set the TransactionData object
//                .build(); // Build the TicketRequestData object
//
//
//        grpcService.getTicket(GrpcConfig.getBlockingStub(), ticketRequest);
//
//    }
//
//
//}