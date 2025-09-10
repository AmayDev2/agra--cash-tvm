package com.amay.tom.grpc;//package com.amay.tom.grpc;
//
//import com.amay.tom.model.QRTicket;
//import com.amay.tom.service.qrDataGenerator.QRDataGenerator;
//import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
////import org.ticket.tg.Ticketdata;
////import org.ticket.tg.ticketsGrpc;
//
//public class GrpcServiceTest {
//
//
//    public QRTicket[] getTicket(ticketsGrpc.ticketsBlockingStub blockingStub, Ticketdata.TicketRequestData ticketRequest){
//
//        try {
//            var response = blockingStub.generateTicket(ticketRequest);
//
//            if (response.getListCount() > 0) {  // Ensure there's at least one item
//                Ticketdata.TicketData ticket = response.getList(0);  // Access the first item
//                //System.out.println("Ticket Order ID: " + ticket.getOrderId());
//                //System.out.println("QR Data: " + ticket.getQrData());  // Example of another property
////                //System.out.println("Ticket Version: " + ticket.getTicketVer());  // Another example
//                 QRDataGenerator qrDataGenerator=new ImplQRDataGenerator();
//                    QRTicket qrTicket=qrDataGenerator.getTgTicketByQRData(ticket.getQrData());
//            } else {
//                //System.out.println("No tickets found in the response.");
//            }
//        } catch (Exception e) {
//            System.err.println("Error during gRPC call: " + e.getMessage());
//            e.printStackTrace();  // Useful for debugging
//        }
//
//
//        return null;
//    }
//
//
//
//}
