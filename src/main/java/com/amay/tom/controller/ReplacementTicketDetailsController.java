package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.StationData;
import com.amay.tom.service.devices.PeripheralMonitor;
import com.amay.tom.service.print.impl.ImplPrintTicket;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.folder.NewFolder;
import com.amay.tom.utils.time.TimeUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.tinylog.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ReplacementTicketDetailsController {

    @FXML
    private Text destination;

    @FXML
    private Button duplicateTicket;

    @FXML
    private Text origin;

    @FXML
    private Text qrSaleDate;

    @FXML
    private Text qrValidity;

    @FXML
    private Text quantity;

    @FXML
    private Text status,statusText;

    @FXML
    private Text ticketCost;

    @FXML
    private Text ticketId;

    @FXML
    private Text ticketType;


    private TicketsDto ticketsDto;
    private BorderPane borderPane;

    public ReplacementTicketDetailsController(TicketsDto ticketsDto,
                                              BorderPane borderPane) {
       this.ticketsDto=ticketsDto;
       this.borderPane=borderPane;
    }


    @FXML
    void onClickDuplicateTicketIssue(ActionEvent event) {
        this.bufferedImage= NewFolder.findTicket(ticketId.getText());
        String status;
        boolean isSuccess;

        if(this.bufferedImage==null) {
            Logger.warn("Ticket Image is null");
        }

        System.out.println("Image :"+this.bufferedImage);

        if(this.ticketsDto.isActive() && /*EnvFile.getPrinterCheck() &&*/ PeripheralMonitor.getPrinterStatus() ){
            Logger.info("Printing Ticket");
            ImplPrintTicket.printImage(this.bufferedImage);
            status="Replaced Successfully";
            isSuccess=true;

        }else {
            isSuccess = false;
            status="Printer not connected";
            Logger.warn("Printer not connected {}", EnvFile.getThermalPrinterModel());
        }
        try {
            FXMLLoader fxmlLoader = ViewFactory.getSuccessPage();
            fxmlLoader.setControllerFactory((x)->new SuccessController(status,isSuccess));
            borderPane.setCenter(fxmlLoader.load());
        }catch (RuntimeException | IOException e){
            e.printStackTrace();
        }

        event.consume();

    }

    @FXML
    void initialize() {
//        areaSelectionToggel.setVisible(false);
        if(this.ticketsDto.isActive()) {
            status.setVisible(false);
            statusText.setVisible(false);
        }else{
            status.setText("Not an active Ticket");
            duplicateTicket.setDisable(true);
        }

        setData(ticketsDto);
    }

    private BufferedImage bufferedImage=null;

    private void setData( TicketsDto qrTicket){
//
//        paid.setSelected(true);
//        unpaid.setSelected(false);
        this.ticketId.setText(qrTicket.getTicketId());
        this.origin.setText(StationData.getInstance().getStation(qrTicket.getInStation()).getStationName());
        this.destination.setText(StationData.getInstance().getStation(qrTicket.getOutStation()).getStationName());
        this.qrSaleDate.setText(TimeUtil.epochMilliToFormattedSystemTime(String.valueOf(qrTicket.getIssueAt()),null));
//        this.qrValidity.setText(TimeUtil.epochMilliToFormattedSystemTime(String.valueOf(qrTicket.getValidUntil()),null));
        this.qrValidity.setText("120 minutes after entry");
        this.quantity.setText(String.valueOf(qrTicket.getQuantity()));
        this.ticketCost.setText("₹ "+qrTicket.getAmount()+" /-");
        this.ticketType.setText(qrTicket.getTicketType());


    }
}
