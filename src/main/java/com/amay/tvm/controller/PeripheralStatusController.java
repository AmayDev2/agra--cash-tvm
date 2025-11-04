package com.amay.tvm.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tvm.backend.enums.ConnectionStatus;
import com.amay.tvm.backend.enums.Peripherals;
import com.amay.tvm.util.Peripheral.PeriStatusInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class PeripheralStatusController {

    @FXML
    private TableView<PeriStatusInfo> periStatusTable;
    @FXML
    private TableColumn<PeriStatusInfo, String> colDevice;
    @FXML
    private TableColumn<PeriStatusInfo, String> colStatus;
    ObservableList<PeriStatusInfo> periStatusInfoObservableList = FXCollections.observableArrayList();
    private Agent agent;
    private SceneManager sceneManager;
    private PeripheralEventListener peripheralEventListener;

    public PeripheralStatusController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        periStatusTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colDevice.setCellValueFactory(data -> data.getValue().getDevice());
        colStatus.setCellValueFactory(data -> data.getValue().getStatus());
        for(Peripherals peripherals :Peripherals.values()) {
            periStatusInfoObservableList.add(new PeriStatusInfo(
                    new SimpleStringProperty(peripherals.name()), new SimpleStringProperty(ConnectionStatus.UNKNOWN.name())));
        }
        periStatusTable.setItems(periStatusInfoObservableList);
        peripheralEventListener=new PeripheralEventListener(null,periStatusInfoObservableList);
        agent.getPeripheralMonitor().addDeviceStatusListener(peripheralEventListener);
    }

    @FXML
    private void onBack(ActionEvent actionEvent) {
        agent.getPeripheralMonitor().removeDeviceStatusListener(peripheralEventListener);
        sceneManager.back();
        actionEvent.consume();
    }

    class PeripheralEventListener implements DeviceStatusListener {
        int[] mDeviceStatus;
        ObservableList<PeriStatusInfo> periStatusInfoObservableList;

       PeripheralEventListener(int[] mDeviceStatus, ObservableList<PeriStatusInfo> periStatusInfoObservableList){
        this.mDeviceStatus=mDeviceStatus;
        this.periStatusInfoObservableList=periStatusInfoObservableList;
       }

       @Override
        public void onDeviceStatusChanged(int[] deviceStatus) {
            int index;
            for (index = 0; null != this.mDeviceStatus && index < deviceStatus.length; index++) {
                if (this.mDeviceStatus[index] != deviceStatus[index])
                    break;
            }

            if (null == this.mDeviceStatus || index != deviceStatus.length) {
                this.mDeviceStatus = deviceStatus;
                int idx=0;
                for(Peripherals peripherals : Peripherals.values()){
                    periStatusInfoObservableList.get(idx++).getStatus().set(
                            mDeviceStatQus[peripherals.getIndex()]>0?
                                    ConnectionStatus.CONNECTED.name():
                                    ConnectionStatus.DISCONNECTED.name());
                }
            }
        }

    }
}
