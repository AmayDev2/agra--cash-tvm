package com.amay.tom.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.maintenance.service.component.MaintenanceFeatures;
import com.amay.tom.maintenance.service.component.StatusWindowPopup;
import com.amay.tom.maintenance.service.component.model.StatusWindowModel;
import com.amay.tom.service.devices.PeripheralMonitor;
import com.amay.tom.service.devices.device.PrinterStatus;
import javafx.scene.input.MouseEvent;

public class MaintenanceModuleTest implements MaintenanceFeatures {

    private PeripheralMonitor peripheralMonitor;
    private Agent agent;
    public MaintenanceModuleTest(PeripheralMonitor peripheralMonitor, Agent agent) {
        this.peripheralMonitor = peripheralMonitor;
        this.agent = agent;
    }



    public void onEFTClick(MouseEvent mouseEvent) {
        System.out.println("EFT clicked");
        boolean isConnected= PrinterStatus.getPrinterStatus();
        new StatusWindowPopup(null,new StatusWindowModel(isConnected,"Recheck","EFT"),agent.getThreadPool()).show();
    }

    public void onQRPrinterClick(MouseEvent mouseEvent) {
        System.out.println("QR clicked");
        boolean isConnected= PrinterStatus.getPrinterStatus();

        new StatusWindowPopup(null,new StatusWindowModel(isConnected,"Recheck","QR Printer"), agent.getThreadPool()).show();
    }

    public void onReceiptClick(MouseEvent mouseEvent) {
        System.out.println("Receipt clicked");
        boolean isConnected= PrinterStatus.getPrinterStatus();
        new StatusWindowPopup(null,new StatusWindowModel(isConnected,"Recheck","Receipt printer"), agent.getThreadPool()).show();
    }

    public void onCSEClick(MouseEvent mouseEvent) {
        System.out.println("CSE clicked");
        boolean isConnected= PrinterStatus.getPrinterStatus();
        new StatusWindowPopup(null,new StatusWindowModel(isConnected,"Recheck","CSE"), agent.getThreadPool()).show();
    }

    public void onPoleClick(MouseEvent mouseEvent) {
        System.out.println("Pole clicked");
        boolean isConnected= PeripheralMonitor.poleDisplayConnected();
        new StatusWindowPopup(null,new StatusWindowModel(isConnected,"Recheck","Pole"), agent.getThreadPool()).show();
    }

    public void onScannerClick(MouseEvent mouseEvent) {
        System.out.println("Scanner clicked");
        boolean isConnected=PeripheralMonitor.scannerConnected();
        new StatusWindowPopup(null,new StatusWindowModel(isConnected,"Recheck","QR Scanner"), agent.getThreadPool()).show();
        mouseEvent.consume();
    }


}
