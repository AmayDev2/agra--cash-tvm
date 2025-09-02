package com.amay.tom.pdu.controller.command;

import com.amay.tom.ViewFactory;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.pdu.controller.PDUController;
import com.amay.tom.pdu.controller.StationMode;
import com.amay.tom.pdu.controller.command.AbnormalStationModeController;
import com.amay.tom.pdu.controller.command.PDUCommand;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.network.monitorandcontrol.SpecialMode;

public class AbnormalStationModeCommand implements PDUCommand {
    private DeviceOperationMode stationMode=null;

    public AbnormalStationModeCommand(DeviceOperationMode stationMode){
        this.stationMode=stationMode;
    }

    @Override
    public void execute(PDUController pduController) {
        System.out.println("Station mode for command  "+stationMode.getDeviceStatusName());
        if(DeviceOperationMode.STATION_CLOSE.equals(stationMode)){
            FXMLLoader loader=ViewFactory.getAbnormalStationMode();
            loader.setControllerFactory(x->new AbnormalStationModeController(StationMode.STATION_CLOSED));
            Platform.runLater(() -> pduController.showView(loader));

        }else if(DeviceOperationMode.EMERGENCY.equals(stationMode)){
            FXMLLoader loader=ViewFactory.getAbnormalStationMode();
            loader.setControllerFactory(x->new AbnormalStationModeController(StationMode.EMERGENCY));
            Platform.runLater(() -> pduController.showView(loader));

        }else if(DeviceOperationMode.OUT_OF_SERVICE.equals(stationMode)) {
            FXMLLoader loader=ViewFactory.getAbnormalStationMode();
            loader.setControllerFactory(x->new AbnormalStationModeController(StationMode.OUT_OF_SERVICE));
            Platform.runLater(() -> pduController.showView(loader));

        }else if(DeviceOperationMode.SHIFT_NOT_ACTIVE.equals(stationMode)) {
            FXMLLoader loader=ViewFactory.getAbnormalStationMode();
            loader.setControllerFactory(x->new AbnormalStationModeController(StationMode.SHIFT_NOT_ACTIVE));
            Platform.runLater(() -> pduController.showView(loader));

        }else{
            Platform.runLater(() -> pduController.showView(ViewFactory.getPDUWelcomePage()));
        }


    }
}
