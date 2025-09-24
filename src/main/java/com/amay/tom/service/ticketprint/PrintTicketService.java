package com.amay.tom.service.ticketprint;

import com.amay.printer.PayReceipt;
import com.amay.printer.PrinterCommandDispatcher;
import com.amay.printer.Response.BaseResponse;
import com.amay.printer.Response.ImagePrintResponse;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.listener.PrintProgressListener;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.adjust.AdjustedTicket;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.model.tickets.PostGeneratedTicket;
import com.amay.tom.model.tickets.UIQRTicket;
import com.amay.tom.service.chield.ticketservice.ImplTicketService;
import com.amay.tom.service.print.impl.ImplPrintTicket;
import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
import com.amay.tom.service.qrService2.TicketInfo;
import com.amay.tom.service.qrservice.Impl.ImplQRService;
import com.amay.tom.service.qrservice.QRService;
import com.amay.tom.utils.folder.NewFolder;
import com.amay.tom.utils.image.ImageUtils;
import com.amay.tom.utils.time.TimeUtil;
import com.amay.tvm.backend.enums.LoggerTag;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.tinylog.Logger;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintTicketService {
    private ArrayList<GeneratedTicket> generatedTicket;
    private PaymentResponse paymentResponse;
    private final ArrayList<UIQRTicket> uiqrTickets;
    private final QRService qrService ;
    private final ImplTicketService implTicketService;
    private final Agent agent;


    public PrintTicketService(ArrayList<GeneratedTicket> generatedTicket, PaymentResponse paymentResponse, Agent agent) {
        this.generatedTicket = generatedTicket;
        this.paymentResponse = paymentResponse;
        uiqrTickets = new ArrayList<>();
        qrService = new ImplQRService();
        implTicketService = new ImplTicketService(new ImplQRDataGenerator());
        this.agent = agent;
    }

    public void printTicket(PrintProgressListener listener) {
            for (GeneratedTicket ticket : generatedTicket) {
                PostGeneratedTicket postGeneratedTicket = (PostGeneratedTicket) ticket;
                UIQRTicket uiqrTicket = new UIQRTicket();
                uiqrTicket.setTicketId(postGeneratedTicket.getTicketId());
                uiqrTicket.setIssuedAt(TimeUtil.epochMilliToFormattedSystemTime(String.valueOf(postGeneratedTicket.getProperTicket().getIssuedAt()), "dd-MM-yyyy HH:mm:ss"));
                uiqrTicket.setValidUntil(TimeUtil.epochMilliToFormattedSystemTime(String.valueOf(postGeneratedTicket.getProperTicket().getValidUntil()), "dd-MM-yyyy HH:mm:ss"));
                uiqrTicket.setPrice(String.valueOf(postGeneratedTicket.getProperTicket().getPrice()));
                uiqrTicket.setQuantity(String.valueOf(postGeneratedTicket.getProperTicket().getQuantity()));
                uiqrTicket.setSource(postGeneratedTicket.getProperTicket().getSource().getStationName());
                uiqrTicket.setDestination(postGeneratedTicket.getProperTicket().getDestination().getStationName());
                uiqrTicket.setTicketType(postGeneratedTicket.getProperTicket().getTicketType());
                uiqrTicket.setQrCode(this.getQRImage(postGeneratedTicket.getQrCodeString(), postGeneratedTicket.getTicketId()));
                uiqrTicket.setQrData(postGeneratedTicket.getQrCodeString());
                this.uiqrTickets.add(uiqrTicket);
            }
                printTicketWithReceipt(listener);
    }

    private void printTicketWithReceipt(PrintProgressListener runnable){
        AtomicInteger count = new AtomicInteger();
        List<QRTicket> qrTickets = getQrTickets();
        ImagePrintResponse response=(ImagePrintResponse)PrinterCommandDispatcher.INSTANCE.printText(qrTickets,paymentResponse.getDenomination()>0?new PayReceipt() {
            @Override
            public String formatedText() {
            String formated= """            
                                    PAYMENT RECEIPT
                                 
                        Denomination    : Rs. %s
                        Order Id        : %s
                        Receipt Id      : %s
                        Payment Mode    : %s
                        Ticket Amount   : Rs. %s
                        Status          : %s
                        Date-Time       : %s
                        TVM             : %s  
                        """;
        return String.format(formated,paymentResponse.getDenomination(),paymentResponse.getOrderId(),paymentResponse.getTransactionId().split("-")[0],paymentResponse.getPaymentMode(),paymentResponse.getAmount(),paymentResponse.isSuccess()?"SUCCESS":"FAILED",paymentResponse.getTransactionTime(), SystemConfig.getInstance().getCurrentEquipment().getEquipmentId());
            }
        }:null);

        if(!response.isSuccess()){
            Logger.tag(LoggerTag.APP).error("Could not print all tickets :{}", Arrays.toString(response.getImagesName().toArray()));
        }

    }

    private List<QRTicket> getQrTickets() {
        List<QRTicket> qrTickets=new ArrayList<>();
        for (UIQRTicket uiqrTicket : uiqrTickets) {
            QRTicket qrTicket = new QRTicket(uiqrTicket.getTicketId(), uiqrTicket.getIssuedAt(), uiqrTicket.getValidUntil(), uiqrTicket.getSource(), uiqrTicket.getDestination(), uiqrTicket.getTicketType().getTicketTypeName(), paymentResponse.getPaymentMode(), uiqrTicket.getPrice(), uiqrTicket.getQrCode());
            qrTicket.setQty(Integer.parseInt(uiqrTicket.getQuantity()));
            qrTicket.setQrCodeData(uiqrTicket.getQrData());
           qrTickets.add(qrTicket);
        }
        return qrTickets;
    }

    private void tempPrintTicket(PrintProgressListener runnable, int size){
        AtomicInteger count = new AtomicInteger();
        for (UIQRTicket uiqrTicket : uiqrTickets) {
            QRTicket qrTicket = new QRTicket(uiqrTicket.getTicketId(), uiqrTicket.getIssuedAt(), uiqrTicket.getValidUntil(), uiqrTicket.getSource(), uiqrTicket.getDestination(), uiqrTicket.getTicketType().getTicketTypeName(), paymentResponse.getPaymentMode(), uiqrTicket.getPrice(), uiqrTicket.getQrCode());
            qrTicket.setQty(Integer.parseInt(uiqrTicket.getQuantity()));
            qrTicket.setQrCodeData(uiqrTicket.getQrData());
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            PrinterCommandDispatcher.INSTANCE.printText(qrTicket);
        }
        ////System.out.println("sold stock :"+FareMedium.QR.getFareMediumSale()+" "+FareMedium.NCMC.getFareMediumSale());
        //agent.getScuService().pushTotalStock(ScuDataMapper.getStockSoldRequest(agent.getShift().getShiftId(),agent.getSystemConfig().getCurrentEquipment().getEquipmentId(),FareMedium.QR.getFareMediumSale(),FareMedium.NCMC.getFareMediumSale()));
    }

    private Image getQRImage(String encryptQR,String ticketId) {
        Image image = null;
        //4-generate QR Generation
        try {
            BufferedImage qrImage = qrService.createQRCode(null, encryptQR, 300, "png");
            //5-save QR image TODO: save image with id
            String folderPath = NewFolder.createTodayFolder();
            ImageUtils.saveBufferedImage(qrImage, folderPath + "\\" + "QR"+ticketId+ ".png");
            image = SwingFXUtils.toFXImage(qrImage, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public void printPaymentReceipt() {
        ImplPrintTicket.printText( "Payment Id: "+paymentResponse.getOrderId()+"\n"+
                                        "OrderId: "+paymentResponse.getOrderId()+"\n"+
                                        "Amount: "+paymentResponse.getAmount()+"\n"+
                                        "Status: "+paymentResponse.getStatus());

    }
}
