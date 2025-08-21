package com.amay.tom.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.grpc.scugrpc.ScuService;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.pdu.controller.command.AbnormalStationModeCommand;
import com.amay.tom.pdu.controller.command.CollectTicketCommand;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.service.ticketprint.PrintTicketService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FreeTicketPrintController {

//    private BufferedImage ticketImage;

    private PrintTicketService printTicketService;


    public FreeTicketPrintController(ArrayList<GeneratedTicket> generatedTicket, PaymentResponse paymentResponse, Agent agent) {
        printTicketService = new PrintTicketService(generatedTicket, paymentResponse,agent);

    }

    @FXML
    void initialize() {

        printTicketService.printTicket();
        new Thread(()->PDUCommandDispatcher.INSTANCE.dispatch(new CollectTicketCommand())).start();
    }

    public void onClickPaidFreeTicket(ActionEvent actionEvent) {
        printTicketService.printPaymentReceipt();
//        if(EnvFile.getPrinterCheck() && PeripheralMonitor.getPrinterStatus() ){
//            Logger.info("Printing Ticket");
//            Platform.runLater(() -> ImplPrintTicket.printImage(ticketImage));
//        }else {
//            Logger.warn("Printer not connected {}", EnvFile.getThermalPrinterModel());
//        }

    }

    public void setData(BufferedImage ticketImage){
//        this.ticketImage=ticketImage;

        }



}
