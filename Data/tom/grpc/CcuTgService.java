package com.amay.tom.grpc;

import com.amay.tom.database.SQLiteConnection;
import com.amay.tom.model.MetroTicket;
import com.amay.tom.model.QRTicket;
import com.amay.tom.repository.TicketsRepository;
import com.amay.tom.service.qrDataGenerator.QRDataGenerator;
import com.amay.tom.service.qrservice.Impl.ImplQRService;
import com.amay.tom.service.qrservice.QRService;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.tinylog.Logger;
import org.transaction.qr.IssueTicket;
import org.transaction.qr.OperatorData;
import org.transaction.qr.TicketBaseData;
import org.transaction.qr.TicketData;

import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CcuTgService {
    private QRDataGenerator qrDataGenerator=null;
    private QRService qrService=null;

    public CcuTgService(QRDataGenerator qrDataGenerator){
        super();
        this.qrDataGenerator=qrDataGenerator;
        qrService=new ImplQRService();
    }


    @Deprecated
    public QRTicket[] getTicket(org.transaction.qr.QrTransactionGrpc.QrTransactionBlockingStub blockingStub, IssueTicket ticketRequest) {



            var response = blockingStub.issueTicket(ticketRequest);
            List<QRTicket> qrTickets=new ArrayList<QRTicket>();
try {
    if (response.getListCount() > 0) {  // Ensure there's at least one item
        TicketData ticket = response.getList(0);  // Access the first item
        Logger.debug("Ticket Order ID: {}", ticket.getOrderId());
        Logger.debug("QR Data: {}", ticket.getQrData());  // Example of another property
//                Logger.debug("Ticket Id: {}", ticket.getTicketVer());  // Another example
        BufferedImage bufferedImage = qrService.createQRCode(null, ticket.getQrData(), 200, "png");
        QRTicket qrTicket = this.qrDataGenerator.getTgTicketByQRData(ticket.getQrData());
        Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
        qrTicket.setQrCodeData(ticket.getQrData());
        qrTickets.add(qrTicket.setQrCode(fxImage));

        // Insert the ticket into the local database
        Connection connection = SQLiteConnection.INSTANCE.getConnection();
        TicketsRepository.getInstance().insertTicket(connection, qrTicket, ticket.getOrderId());
        connection.close();


    } else {
        Logger.warn("No tickets found in the response.");
    }

    return qrTickets.toArray(new QRTicket[0]);
}catch (Exception e){
    e.printStackTrace();
}
return null;
    }


    public IssueTicket getTicketRequestData(MetroTicket metroTicket, String orderId){

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





}
