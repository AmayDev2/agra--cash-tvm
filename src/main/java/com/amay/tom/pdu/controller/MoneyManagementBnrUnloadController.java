package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tom.utils.tasks.BuzzerTask;
import com.amay.tvm.backend.service.BnrFinanceMaintenance;
import com.amay.tvm.bnr.BNRIntegration;
import com.jxfs.events.JxfsException;
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class MoneyManagementBnrUnloadController  {
    private final Agent agent;
    private final SceneManager sceneManager;
    private ListenBnrEvent listenBnrEvent;
    public MoneyManagementBnrUnloadController(Agent agent, SceneManager sceneManager) {
        this.sceneManager=sceneManager;
        this.agent=agent;
        listenBnrEvent=new ListenBnrEvent(this);
        agent.getPeripheralMonitor().addDeviceStatusListener(listenBnrEvent);

    }

    public void onBnrUnLoad(ActionEvent actionEvent) throws JxfsException {
        BNRIntegration.bnrUnloadRecycler();
        actionEvent.consume();
    }

    public void onR1(ActionEvent actionEvent) {
        BNRIntegration.bnrUnload("RE3"); // RE-3->6
        actionEvent.consume();
    }

    public void onR2(ActionEvent actionEvent) {
        BNRIntegration.bnrUnload("RE4");
        actionEvent.consume();
    }

    public void onR3(ActionEvent actionEvent) {
        BNRIntegration.bnrUnload("RE5"); // RE-3->6
        actionEvent.consume();
    }

    public void onR4(ActionEvent actionEvent) {
        BNRIntegration.bnrUnload("RE6"); // RE-3->6
        actionEvent.consume();
    }

    public void onBackBnr(ActionEvent actionEvent) {
        agent.getPeripheralMonitor().removeDeviceStatusListener(listenBnrEvent);
        sceneManager.back();
        actionEvent.consume();

    }


    static class ListenBnrEvent implements DeviceStatusListener {
        private int[] mDeviceStatus;
        MoneyManagementBnrUnloadController controller;
        public ListenBnrEvent(MoneyManagementBnrUnloadController pduController){
            this.controller=pduController;
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
                if(deviceStatus[7]==0) {
                    // save in DB
                    BNRIntegration.bnrSetDepositZero();
                    // remove from DB
                }

            }
        }
    }


}
