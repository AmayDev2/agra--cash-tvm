package com.amay.tom.controller.components;

import com.amay.tom.config.Versions;
import com.amay.tom.model.version.MasterConfigInfo;
import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tom.service.devices.PeripheralMonitor;
import com.amay.tom.utils.helper.Helper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.tinylog.Logger;

public class StatusBottomBarView {
    @FXML
    private Text fareTableVersion;
    @FXML
    private Text softwareVersion;
    @FXML
    private Text parameterVersion;
    @FXML
    private Button scuConnectedImage;

    @FXML
    private Button ccuConnectedImage;

    @FXML
    private Button readerConnectedImage;

    @FXML
    private Button scannerConnectedImage;

    @FXML
    private Button printerConnectedImage;

    @FXML
    private Button pduConnectedImage;

    @FXML
    private Button cashDrawerConnectedImage;

    @FXML
    private Button upsConnectedImage;

    private Versions versions;

    private MasterConfigInfo masterConfigInfo;


    @FXML
    private void initialize() {
        Logger.debug("StatusBottomBarView initialized");
//        softwareVersion.setText(versions.getVersion());
        softwareVersion.setText(masterConfigInfo.getTomSwVer());
        fareTableVersion.setText(masterConfigInfo.getFareConfig());
        parameterVersion.setText(masterConfigInfo.getConfigVer());
    }

    public StatusBottomBarView(PeripheralMonitor peripheralMonitor, Versions versions, MasterConfigInfo masterConfigInfo) {
        peripheralMonitor.addDeviceStatusListener(new UIDeviceListener(this));
        this.versions = versions;
        this.masterConfigInfo=masterConfigInfo;
    }

    private void setPeripheralStatus(Button button, boolean isActive) {
        Logger.debug("Setting peripheral status: {} {}", button, isActive);
        if (isActive) {
            button.setStyle("-fx-background-color: green;");
        } else {
            button.setStyle("-fx-background-color: red;");
        }
    }

    void updateStatus(int[] deviceStatus) {
        Logger.debug("In Updating device status on self: {}", Helper.ObjectToJson(deviceStatus));

        setPeripheralStatus(scannerConnectedImage, deviceStatus[0] == 1);
        setPeripheralStatus(printerConnectedImage, deviceStatus[1] == 1);
        setPeripheralStatus(scuConnectedImage, deviceStatus[2] == 1);
        setPeripheralStatus(ccuConnectedImage, deviceStatus[3] == 1);
        setPeripheralStatus(readerConnectedImage, deviceStatus[4] == 1);
        setPeripheralStatus(pduConnectedImage, deviceStatus[5] == 1);
        setPeripheralStatus(cashDrawerConnectedImage, deviceStatus[6] == 1);
        setPeripheralStatus(upsConnectedImage, deviceStatus[7] == 1);
    }
}


class UIDeviceListener implements DeviceStatusListener {
    private int[] mDeviceStatus ;
    private final StatusBottomBarView controller;
    public UIDeviceListener(StatusBottomBarView controller) {
        this.controller = controller;
    }
    @Override
    public void onDeviceStatusChanged(int[] deviceStatus) {

        int index ;
        for(index=0; null!=this.mDeviceStatus && index < deviceStatus.length; index++){
            if(this.mDeviceStatus[index] != deviceStatus[index])
                break;
        }

        if(null==this.mDeviceStatus || index != deviceStatus.length){
            this.mDeviceStatus= deviceStatus;
            this.controller.updateStatus(deviceStatus);
        }
    }

}
