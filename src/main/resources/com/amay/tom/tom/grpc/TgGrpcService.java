//package com.amay.tom.grpc;
//
//import com.amay.tom.config.SystemConfig;
//import com.amay.tom.database.DatabaseConnector;
//import com.amay.tom.database.SQLiteConnection;
//import com.amay.tom.model.MetroTicket;
//import com.amay.tom.model.QRTicket;
//import com.amay.tom.repository.TicketsRepository;
//import com.amay.tom.service.qrDataGenerator.QRDataGenerator;
//import com.amay.tom.service.qrservice.Impl.ImplQRService;
//import com.amay.tom.service.qrservice.QRService;
//import javafx.embed.swing.SwingFXUtils;
//import javafx.scene.image.Image;
//import lombok.SneakyThrows;
////import org.ticket.tg.Ticketdata;
////import org.ticket.tg.ticketsGrpc;
////import org.ticket.tg.Ticketdata;
////import org.ticket.tg.ticketsGrpc;
//import org.tinylog.Logger;
//
//import com.google.protobuf.util.JsonFormat;
//
//import java.awt.image.BufferedImage;
//import java.sql.Connection;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class TgGrpcService extends GrpcServiceTest{
//    private QRDataGenerator qrDataGenerator=null;
//    private QRService qrService=null;
//
//    public TgGrpcService(QRDataGenerator qrDataGenerator){
//        super();
//        this.qrDataGenerator=qrDataGenerator;
//        qrService=new ImplQRService();
//
//    }
//
//    @SneakyThrows   // Lombok`s annotation to avoid writing try-catch blocks for checked exceptions (like InterruptedException)
//    @Override
//    public QRTicket[] getTicket(ticketsGrpc.ticketsBlockingStub blockingStub, Ticketdata.TicketRequestData ticketRequest){
//
//
//
//            var response = blockingStub.generateTicket(ticketRequest);
//            List<QRTicket> qrTickets=new ArrayList<QRTicket>();
//
//            if (response.getListCount() > 0) {  // Ensure there's at least one item
//                Ticketdata.TicketData ticket = response.getList(0);  // Access the first item
//                Logger.debug("Ticket Order ID: {}", ticket.getOrderId());
//                Logger.debug("QR Data: {}", ticket.getQrData());  // Example of another property
////                Logger.debug("Ticket Id: {}", ticket.getTicketVer());  // Another example
//                BufferedImage bufferedImage=qrService.createQRCode(null, ticket.getQrData(),200,"png");
//                QRTicket qrTicket=this.qrDataGenerator.getTgTicketByQRData(ticket.getQrData());
//                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
//                qrTicket.setQrCodeData(ticket.getQrData());
//                qrTickets.add(qrTicket.setQrCode(fxImage));
//
//                // Insert the ticket into the local database
//                Connection connection = SQLiteConnection.INSTANCE.getConnection();
//                TicketsRepository.getInstance().insertTicket(connection,qrTicket);
//                connection.close();
//
//
//            } else {
//                Logger.warn("No tickets found in the response.");
//            }
//
//            return qrTickets.toArray(new QRTicket[0]);
//    }
//
//    public Ticketdata.TicketRequestData getTicketRequestData(MetroTicket metroTicket){
//
//        String currentTime = String.valueOf(Instant.now().getEpochSecond());
//        Logger.info("Current Time: {}",currentTime);
//        // Create an OperatorData object
//        Ticketdata.OperatorData operatorData = Ticketdata.OperatorData.newBuilder()
//                .setLineId(3) // Set the line ID
//                .setStationId(Integer.parseInt(SystemConfig.getInstance().getCurrentStation().getStationId().split("st")[1])) //TODO:  check datatype
//                .setEquipId(Integer.parseInt(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())) // Set the equipment ID
//                .setOprId(SystemConfig.getInstance().getCurrentUser().getUId()) // Set the operator ID
//                .setShiftId(32) // Set the shift ID
//                .build(); // Build the OperatorData object
//
//// Create a TransactionData object
//        Ticketdata.TransactionData txnData = Ticketdata.TransactionData.newBuilder()
//                .setOrderId(String.valueOf(UUID.randomUUID())) // Set the order ID
//                .setPayType(1) // Set the payment type
//                .setSrcId(Integer.parseInt(metroTicket.getSource().split("st")[1])) // Set the source ID
//                .setDestId(Integer.parseInt(metroTicket.getDestination().split("st")[1])) // Set the destination ID
//                .setTicketType(Integer.parseInt(metroTicket.getTicketType().getTicketTypeId())) // Set the ticket type
//                .setIssueDateTime(currentTime) // Set the issue date/time
//                .setQrIssueMode(1) // Set the QR issue mode
//                .setPassCount(metroTicket.getTicketQuantity()) // Set the number of passengers
//                .setTicketFare(metroTicket.getFare()) // Set the ticket fare
//                .setTxnAmt(metroTicket.getFare()) // Set the transaction amount
//                .setDisAmt(0) // Set the discount amount
//                .build(); // Build the TransactionData object
//
//// Create a TicketRequestData object with OperatorData and TransactionData
//        Ticketdata.TicketRequestData ticketRequest = Ticketdata.TicketRequestData.newBuilder()
//                .setOperatorData(operatorData) // Set the OperatorData object
//                .setTxnData(txnData) // Set the TransactionData object
//                .build(); // Build the TicketRequestData object
//
//        return ticketRequest;
//    }
//}
