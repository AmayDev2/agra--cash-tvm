package com.amay.tom.pdu;

import com.amay.printer.PrinterCommandDispatcher;
import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.maintenance.service.component.StatusWindowPopup;
import com.amay.tom.maintenance.service.component.model.StatusWindowModel;
import com.amay.tom.pdu.controller.UPSTestController;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.service.chield.ticketservice.ImplTicketService;
import com.amay.tom.service.devices.PeripheralMonitor;
import com.amay.tom.service.devices.device.PrinterStatus;
import com.amay.tom.service.print.impl.PrinterService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceTestController {

    private final Agent agent;
    private final SceneManager sceneManager;

    public MaintenanceTestController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }
    private PeripheralMonitor peripheralMonitor;

    @FXML
    private
    void onBNRTest(ActionEvent event) {

    }

    @FXML
    private void onCoinHopper(ActionEvent event) {
    }

    @FXML
    private void onPrinterTest(ActionEvent event) {
        boolean isConnected=PrinterCommandDispatcher.INSTANCE.isConnected();
        List<String> list=this.getStatus();

        new StatusWindowPopup(null,new StatusWindowModel(isConnected,"Re-printing","Printer",list),agent.getThreadPool()).show();
//        PrinterCommandDispatcher.INSTANCE.testPrint();
        event.consume();
    }

    private List<String> getStatus() {
        List<String> list = new ArrayList<>();

        try {
            com.custom.wndapijwrap.PrinterStatus status = PrinterCommandDispatcher.INSTANCE.getStatus();

            // Paper related
            if (status.StsNOPAPER) {
                list.add("No Paper");
            }
            if (status.StsNEARENDPAP) {
                list.add("Paper Near End");
            }
            if (status.StsTICKETOUT) {
                list.add("Ticket Taken Out");
            }
            if (status.StsVIRTUALPAPEREND) {
                list.add("Virtual Paper End");
            }

            // Hardware related
            if (status.StsNOHEAD) {
                list.add("No Print Head Detected");
            }
            if (status.StsNOCOVER) {
                list.add("Cover Open");
            }

            // Operation related
            if (status.StsSPOOLING) {
                list.add("Spooling In Progress");
            }
            if (status.StsPAPERROLLING) {
                list.add("Paper Rolling");
            }

            // Buttons
            if (status.StsLFPRESSED) {
                list.add("Line Feed Button Pressed");
            }
            if (status.StsFFPRESSED) {
                list.add("Form Feed Button Pressed");
            }

            // Errors and warnings
            if (status.StsOVERTEMP) {
                list.add("Printer Over Temperature");
            }
            if (status.StsHLVOLT) {
                list.add("High Voltage Detected");
            }
            if (status.StsPAPERJAM) {
                list.add("Paper Jam Detected");
            }
            if (status.StsCUTERROR) {
                list.add("Cutter Error");
            }
            if (status.StsRAMERROR) {
                list.add("RAM Error");
            }
            if (status.StsEEPROMERROR) {
                list.add("EEPROM Error");
            }

            // If no issue detected
            if (list.isEmpty()) {
                list.add("Printer Ready");
            }

        } catch (Exception e) {
            list.clear();
            list.add("Printer Disconnected");
        }

        return list;
    }


    @FXML
    private void onUPSTest(ActionEvent event) {
        FXMLLoader fxmlLoader= ViewFactory.getUPSTestLoad();
        UPSTestController controller=new UPSTestController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        event.consume();
    }

    @FXML
    private void onPIdTest(ActionEvent event) {
    }

    @FXML
    private void onFeigTest(ActionEvent event) {
    }

    @FXML
    private void onCommstest(ActionEvent event) {
    }

    @FXML
    private void onRestart(ActionEvent event) {
    }

    @FXML
    private void onBack(ActionEvent event) {
        this.sceneManager.back();
        event.consume();
    }
}
