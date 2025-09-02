package com.amay.tom.service.chield.ticketservice;

import com.amay.printer.PrinterCommandDispatcher;
import com.amay.tom.ViewFactory;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controller.AgaraTicketController;
import com.amay.tom.database.SQLiteConnection;
import com.amay.tom.exceptions.TicketNotGenerated;
import com.amay.tom.model.MetroTicket;
import com.amay.tom.model.Passenger;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.siftdata.ShiftHeader;
import com.amay.tom.model.siftdata.TicketRecord;
import com.amay.tom.model.station.Station;
import com.amay.tom.repository.QRDataArray;
import com.amay.tom.repository.TicketRecordRepository;
import com.amay.tom.repository.TicketsRepository;
import com.amay.tom.service.chield.ReprintTicket;
import com.amay.tom.service.chield.TicketService;
import com.amay.tom.service.devices.PeripheralMonitor;
import com.amay.tom.service.fare.Fare;
import com.amay.tom.service.print.impl.ImplPrintTicket;
import com.amay.tom.service.qrDataGenerator.QRDataGenerator;
import com.amay.tom.service.qrservice.Impl.ImplQRService;
import com.amay.tom.service.qrservice.QRService;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.folder.NewFolder;
import com.amay.tom.utils.image.ImageUtils;
import com.amay.tom.utils.time.TimeUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import org.tinylog.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImplTicketService implements TicketService, ReprintTicket {
    static int ticketCount = 0;
    //Integer.parseInt(QRDataArray.qrDataArray.get(QRDataArray.qrDataArray!=null && QRDataArray.qrDataArray.size()>=0?QRDataArray.qrDataArray.size()-1:0).getTicketNo());
    QRService qrService;

    private QRDataGenerator qrDataGenerator;

    private TicketsRepository ticketsRepository;

    String filePath = "qrcode.png";
    int size = 200;
    String fileType = "png";


    public ImplTicketService() {
        qrService = new ImplQRService();
    }

    public ImplTicketService(QRDataGenerator qrDataGenerator) {
        this();
        this.qrDataGenerator = qrDataGenerator;
        ticketsRepository = TicketsRepository.getInstance();

//        if (ticketCount == 0) {
//            Connection connection = null;
//            try {
//                SQLiteConnection.INSTANCE.setSQLiteConnection();
//                connection = SQLiteConnection.INSTANCE.getConnection();
//                ticketCount = Integer.parseInt(ticketsRepository.getLastTicketNumber(connection));
//            } catch (InterruptedException e) {
//                ticketCount = 120000;
//                Logger.error("Error in getting last ticket number: {}", e.getMessage());
//            } finally {
//                SQLiteConnection.INSTANCE.releaseConnection(connection);
//                Logger.info("Last Ticket Number: {}", ticketCount);
//            }
//        }


    }


    //This method is used to generate an initial ticket
    @Override
    public MetroTicket generateTicket(Station sourceStation, Station destinationStation, Station station, int noOfPassenger, TicketType selectedTicketType) {
        if (sourceStation == null || destinationStation == null || station == null || noOfPassenger <= 0 || selectedTicketType == null) {
            Logger.error("{} {}", SystemConfig.getInstance().getCurrentUser().getUserId(), "Invalid input data for generating ticket");
            throw new TicketNotGenerated("Invalid input data for generating ticket");
        }

        MetroTicket metroTicket = new MetroTicket(SystemConfig.getInstance().getLineNumber(), station, System.currentTimeMillis() / 1000, (int) ((selectedTicketType.equals(TicketType.RETURN) ? 24 : selectedTicketType.equals(TicketType.FREE) ? .25 : 1)), sourceStation, destinationStation, null, Fare.getTotalFare(sourceStation, destinationStation, noOfPassenger, selectedTicketType), selectedTicketType, noOfPassenger);

        Logger.info("Metro Ticket: {}", metroTicket.toString());
        return metroTicket;

    }

  /*  public QRTicket[] generateAdjustedQRTicket(MetroTicket[] metroTickets) {
        ArrayList<QRTicket> qrTickets = new ArrayList<>();
        try {
            for (MetroTicket metroTicket : metroTickets) {

                //TODO: This is a temporary fix to generate ticket id
                String ticketId = String.format("%06d", ++ticketCount);


                metroTicket.setTicketId(ticketId);
                String qrCodeData = getEncryptedQRData(metroTicket);
                Logger.debug("QR Code Data: {} ?", qrCodeData);
                String filePath = "qrcode.png";
                int size = 200;
                String fileType = "png";

                File qrCodeFile = new File(filePath);

                this.dbUpdate(metroTicket);  //TODO: update the ticket record in the database

                BufferedImage bufferedImage = qrService.createQRCode(qrCodeFile, qrCodeData, size, fileType);

                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                QRTicket qrt = metroTicket.getQRTicket().setQrCode(fxImage);
                qrt.setQrCodeData(qrCodeData);
                qrt.setQty(metroTicket.getTicketQuantity());
                QRDataArray.qrDataArray.add(qrt);
 //               ticketsRepository.insertTicket(SQLiteConnection.INSTANCE.getConnection(), qrt,orderId); //TODO: update the ticket record in the database
//                ticketsRepository.testInsert(SQLiteConnection.INSTANCE.getConnection());
                qrTickets.add(qrt);
            }

        } catch (Exception e) {
            Logger.error("Error in generating QR Code: {}", e.getMessage());
        }
        return qrTickets.toArray(new QRTicket[0]);
    }*/


    public QRTicket[] generateQRTicket(MetroTicket[] metroTickets, String orderId) {
        ArrayList<QRTicket> qrTickets = new ArrayList<>();
        try {
            for (MetroTicket metroTicket : metroTickets) {

                //TODO: This is a temporary fix to generate ticket id
                String ticketId = String.format("%06d", ++ticketCount);


                metroTicket.setTicketId(ticketId);
                String qrCodeData = getEncryptedQRData(metroTicket);
                Logger.debug("QR Code Data: {} ?", qrCodeData);
                String filePath = "qrcode.png";
                int size = 200;
                String fileType = "png";

                File qrCodeFile = new File(filePath);

                this.dbUpdate(metroTicket);  //TODO: update the ticket record in the database

                BufferedImage bufferedImage = qrService.createQRCode(qrCodeFile, qrCodeData, size, fileType);

                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                QRTicket qrt = metroTicket.getQRTicket().setQrCode(fxImage);
                qrt.setQrCodeData(qrCodeData);
                qrt.setQty(metroTicket.getTicketQuantity());
                QRDataArray.qrDataArray.add(qrt);
                ticketsRepository.insertTicket(SQLiteConnection.INSTANCE.getConnection(), qrt,orderId); //TODO: update the ticket record in the database
//                ticketsRepository.testInsert(SQLiteConnection.INSTANCE.getConnection());
                qrTickets.add(qrt);
            }

        } catch (Exception e) {
            Logger.error("Error in generating QR Code: {}", e.getMessage());
        }
        return qrTickets.toArray(new QRTicket[0]);
    }

    @Deprecated
    public boolean reprintTicketByTicketNo(String ticketNo) {
        Logger.info("Reprinting ticket by ticket no: {}", ticketNo);

        Connection connection = null;

        try {
            connection = SQLiteConnection.INSTANCE.getConnection();
            QRTicket qrTicket = ticketsRepository.getTicketByTicketNo(connection, ticketNo);
            if (qrTicket == null) {
                Logger.error("Ticket not found for ticket no: {}", ticketNo);
                return false;
            }

            BufferedImage bufferedImage = qrService.createQRCode(null, qrTicket.getQrCodeData(), size, fileType);
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

            qrTicket.setQrCode(fxImage);

            FXMLLoader fxmlLoader = ViewFactory.getTicket();
            Pane vd = fxmlLoader.load();
//            TicketController ticketController = fxmlLoader.getController();

//            ticketController.setTicketDetails(qrTicket);
            AgaraTicketController agaraTicketController= fxmlLoader.getController();
            agaraTicketController.updateTicketData(qrTicket);


            //TODO:Print the ticket
            if (EnvFile.getPrinterCheck() && PeripheralMonitor.getPrinterStatus()) {
                Logger.info("Printing Ticket");
                ImplPrintTicket.printImage(ImageUtils.nodeToImage(vd));
            }
            else
            {
                Logger.warn("Printer not connected {}", EnvFile.getThermalPrinterModel());
            }

        } catch (Exception e) {
            Logger.error("Error in reprinting ticket: {}", e.getMessage());
            return false;
        }
        return true;

    }

    public QRTicket getTicketByTicketNumber(String ticketNo) {
        Logger.info("Reprinting ticket by ticket no: {}", ticketNo);
        QRTicket qrTicket= null;

        Connection connection = null;

        try {
            connection = SQLiteConnection.INSTANCE.getConnection();
            qrTicket = ticketsRepository.getTicketByTicketNo(connection, ticketNo);
            if (qrTicket == null) {
                Logger.error("Ticket not found for ticket no: {}", ticketNo);
                return null;
            }
//
//            BufferedImage bufferedImage = qrService.createQRCode(null, qrTicket.getQrCodeData(), size, fileType);
//            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
//
//            qrTicket.setQrCode(fxImage);
//
//            FXMLLoader fxmlLoader = ViewFactory.getTicket();
//            Pane vd = fxmlLoader.load();
//            TicketController ticketController = fxmlLoader.getController();
//
//            ticketController.setTicketDetails(qrTicket);
//
//
//            //TODO:Print the ticket
//            if (EnvFile.getPrinterCheck() && PeripheralMonitor.getPrinterStatus()) {
//                Logger.info("Printing Ticket");
//                ImplPrintTicket.printImage(ImageUtils.nodeToImage(vd));
//            } else {
//                Logger.warn("Printer not connected {}", EnvFile.getThermalPrinterModel());
//            }

        } catch (Exception e) {
            Logger.error("Error in reprinting ticket: {}", e.getMessage());
        }
        return qrTicket;

    }

    @Override
    public BufferedImage getImage(QRTicket qrTicket) {

        BufferedImage bufferedImage = null;
        try {
            FXMLLoader fxmlLoader = ViewFactory.getTicket();
            Pane vd = fxmlLoader.load();

            AgaraTicketController agaraTicketController= fxmlLoader.getController();
            agaraTicketController.updateTicketData(qrTicket);
            bufferedImage = ImageUtils.nodeToImage(vd);
            String folderPath = NewFolder.createTodayFolder();

            ImageUtils.saveBufferedImage(bufferedImage, folderPath + "\\" + qrTicket.getTicketNo() + ".png");

        } catch (Exception e) {
            Logger.error("Error in printing ticket: {}", e.getMessage());
            e.getStackTrace();
        }

        return bufferedImage;
    }


    @Override
    public boolean printAndSaveTicket(QRTicket qrTicket) {

        try {
            FXMLLoader fxmlLoader = ViewFactory.getTicket();
            Pane vd = fxmlLoader.load();

            AgaraTicketController agaraTicketController= fxmlLoader.getController();
            agaraTicketController.updateTicketData(qrTicket);
            BufferedImage bufferedImage = ImageUtils.nodeToImage(vd);
            String folderPath = NewFolder.createTodayFolder();

            ImageUtils.saveBufferedImage(bufferedImage, folderPath + "\\" + qrTicket.getTicketNo() + ".png");

            Logger.info("Printer status: {}", PeripheralMonitor.getPrinterStatus());
            //TODO:Print the ticket
            if (/*EnvFile.getPrinterCheck() &&*/ PeripheralMonitor.getPrinterStatus()) {

                Logger.info("Printing Ticket");
//                ImplPrintTicket.printImage(bufferedImage);
                PrinterCommandDispatcher.INSTANCE.printText(qrTicket);
            } else {
                Logger.warn("Printer not connected {}", EnvFile.getThermalPrinterModel());
            }
            return true;
        } catch (Exception e) {
            Logger.error("Error in printing ticket: {}", e.getMessage());
            e.getStackTrace();


        }

        return false;
    }

    @Override
    public ArrayList<MetroTicket> generateTickets(ArrayList<Passenger> passengerList) {
        Logger.debug("Generating tickets for the passengers {}", passengerList.toString());
        ArrayList<MetroTicket> metroTickets = new ArrayList<>();
        for (Passenger passenger : passengerList) {
            MetroTicket metroTicket = generateTicket(passenger.getSource(), passenger.getDestination(), passenger.getSource(), passenger.quantity(), passenger.getTicketType());
            metroTickets.add(metroTicket);
        }

        return metroTickets;
    }


    private int getTicketQuantity(TicketType ticketType, int noOfPassenger) {
        if (ticketType.equals(TicketType.GROUP)) {
            return 1;
        }
        return noOfPassenger;
    }


    //This method is used to get the encrypted QR String
    public String getEncryptedQRData(MetroTicket metroTicket) {
        if (metroTicket.getTicketId() == null || metroTicket.getTicketId().isEmpty()) {
            throw new TicketNotGenerated("Ticket ID is not generated for the ticket ");
        }

        return this.qrDataGenerator.getQRDataByTicketV1(metroTicket);

//         String sum=metroTicket.getIssueDay();
//         String HM=metroTicket.getIssueHM();
//         String source=metroTicket.getSource();
//         String destination=metroTicket.getDestination();
//         String FARE=String.format("%04d",metroTicket.getFare());
//         String EXP_TIME=String.valueOf("01");
//
//
//         String qrData= metroTicket.getMetroNumber()+metroTicket.getStationId()+metroTicket.getEquipmentId()+metroTicket.getEquipmentSerial()+source+destination+sum+
//                 HM+EXP_TIME+FARE+(char)metroTicket.getTicketType().getTicketTypeId()+/*no of passenger*/String.format("%02d",metroTicket.getTicketQuantity())+metroTicket.getTicketId();
//
//         Logger.info("QR Data: {}",qrData);
//            return qrData;
    }


    //upsert ticket record in local database
    private void dbUpdate(MetroTicket metroTicket) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Connection connection = null;
            try {
                connection = SQLiteConnection.INSTANCE.getConnection();
                TicketRecord ticketRecord = TicketRecordRepository.getInstance().readTicketRecord(
                        connection,
                        metroTicket.getTicketType().getTicketTypeId(),
                        metroTicket.getFare(),
                        String.valueOf(ShiftHeader.getInstance().getShiftId()),
                        TimeUtil.getCurrentYearMonthDay()
                );
                Logger.info("Ticket Record: {}", ticketRecord);
                if (ticketRecord == null) {
                    TicketRecordRepository.getInstance().createTicketRecord(
                            connection,
                            new TicketRecord(
                                    metroTicket.getTicketType().getTicketTypeId(),
                                    metroTicket.getFare(),
                                    TimeUtil.getCurrentYearMonthDay(),
                                    1,
                                    ShiftHeader.getInstance().getShiftId()
                            )
                    );
                } else {
                    ticketRecord.setCount(ticketRecord.getCount() + 1);
                    TicketRecordRepository.getInstance().updateTicketRecord(
                            connection,
                            ticketRecord
                    );
                }
            } catch (SQLException e) {
                Logger.error("Error in database operation: {}", e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                SQLiteConnection.INSTANCE.releaseConnection(connection);
            }
        });
        executor.shutdown();
    }

}
