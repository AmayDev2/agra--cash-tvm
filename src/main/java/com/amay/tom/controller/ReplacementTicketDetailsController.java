package com.amay.tom.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.controller.SuccessController;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.replacement.ReplacementDTO;
import com.amay.tom.model.replacement.ReplacementMapper;
import com.amay.tom.model.tickets.QRTicketV2;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.Replacement.ReplacementTicketRepository;
import com.amay.tom.repository.Replacement.ReplacementTicketRepositoryImpl;
import com.amay.tom.repository.StationData;
import com.amay.tom.service.chield.ticketservice.ImplTicketService;
import com.amay.tom.service.devices.PeripheralMonitor;
import com.amay.tom.service.print.impl.ImplPrintTicket;
import com.amay.tom.service.qrDataGenerator.impl.ImplQRDataGenerator;
import com.amay.tom.service.qrservice.Impl.ImplQRService;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.folder.NewFolder;
import com.amay.tom.utils.image.ImageUtils;
import com.amay.tom.utils.time.TimeUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.tinylog.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;

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
    private Text status,statusText,statusColon;

    @FXML
    private Text ticketCost;

    @FXML
    private Text ticketId;

    @FXML
    private Text ticketType;


    private final TicketsDto ticketsDto;
    private final BorderPane borderPane;

    private ImplQRService qrService;
    private Agent agent;

    public ReplacementTicketDetailsController(TicketsDto ticketsDto,
                                              BorderPane borderPane,Agent agent) {
       this.ticketsDto=ticketsDto;
       this.borderPane=borderPane;
       this.agent = agent;
        qrService = new ImplQRService();
    }
    /**
     * Generates a QR image from the encrypted QR data and ticket ID.
     *
     * @param encryptQR The encrypted QR data.
     * @param ticketId  The ticket ID.
     * @return An Image object containing the generated QR code.
     */
    private Image getQRImage(String encryptQR, String ticketId) {
        Image image = null;
        //4-generate QR Generation
        try {
            BufferedImage qrImage = qrService.createQRCode(null, encryptQR, 500, "png");
            //5-save QR image TODO: save image with id
            String folderPath = NewFolder.createTodayFolder();
            ImageUtils.saveBufferedImage(qrImage, folderPath + "\\" + "QR"+ticketId+ ".png");
            image = SwingFXUtils.toFXImage(qrImage, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


    @FXML
    void onClickDuplicateTicketIssue(ActionEvent event) {
        try {
            this.bufferedImage = NewFolder.findTicket(this.ticketsDto.getTicketId());
            String status;
            boolean isSuccess;

            if (this.bufferedImage == null) {
                ImplTicketService implTicketService = new ImplTicketService(new ImplQRDataGenerator());
                QRTicket qrTicket = new QRTicket(this.ticketsDto.getTicketId(), TimeUtil.epochMilliToFormattedSystemTime(String.valueOf(this.ticketsDto.getIssueAt()), "dd-MM-yyyy HH:mm:ss"), String.valueOf(this.ticketsDto.getValidUntil()), StationData.getInstance().getStation(this.ticketsDto.getInStation()).getStationName(), StationData.getInstance().getStation(this.ticketsDto.getOutStation()).getStationName(), this.ticketsDto.getTicketType(), this.ticketsDto.getPaymentMode(), String.valueOf(this.ticketsDto.getAmount()), getQRImage(this.ticketsDto.getQrData(), this.ticketsDto.getTicketId()));
                qrTicket.setQty(this.ticketsDto.getQuantity());
                this.bufferedImage = implTicketService.getImage(qrTicket);
                
            }

            //System.out.println("Image :" + this.bufferedImage);

            if (this.ticketsDto.isActive()
//                    && /*EnvFile.getPrinterCheck() &&*/
//                    PeripheralMonitor.getPrinterStatus()
            ) {
                Logger.info("Printing Ticket");
                ImplPrintTicket.printImage(this.bufferedImage);
                this.insert(this.ticketsDto);
                status = "Replaced Successfully";
                isSuccess = true;

            } else {
                isSuccess = false;
                status = "Printer not connected";
                Logger.warn("Printer not connected {}", EnvFile.getThermalPrinterModel());
            }
            try {
                FXMLLoader fxmlLoader = ViewFactory.getSuccessPage();
                fxmlLoader.setControllerFactory((x) -> new SuccessController(status, isSuccess));
                borderPane.setCenter(fxmlLoader.load());
            } catch (RuntimeException | IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            Logger.error("Error while printing ticket: {}", e.getMessage());
        }
        finally {
            event.consume();
        }
    }

    @FXML
    void initialize() {
//        areaSelectionToggel.setVisible(false);
        if(this.ticketsDto.isActive()) {
            status.setVisible(false);
            statusText.setVisible(false);
            statusColon.setVisible(false);
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
        this.ticketType.setText(TicketType.getTicket(qrTicket.getTicketType()).getProduct().getProductName());


    }

    private void insert(TicketsDto qrTicket){
        ReplacementDTO replacementDTO = new ReplacementDTO();
        replacementDTO.setAmount(qrTicket.getAmount());
        replacementDTO.setTicketType(qrTicket.getTicketType());
        replacementDTO.setTicketNumber(qrTicket.getTicketId());
        replacementDTO.setShiftId(agent.getShift().getShiftId());
        replacementDTO.setDeviceId(agent.getSystemConfig().getCurrentEquipment().getEquipmentId());
        replacementDTO.setOperatorId(agent.getShift().getOperatorId());
        replacementDTO.setCreationDateTime(LocalDateTime.now());
        replacementDTO.setUpdateDateTime(LocalDateTime.now());
        agent.getReplacementTicketRepository().insert(ReplacementMapper.toEntity(replacementDTO));
    }
}
