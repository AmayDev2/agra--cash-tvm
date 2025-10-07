package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.repository.FinanceOperationRepository;
import com.amay.tvm.bnr.BNRIntegration;
import com.jxfs.events.JxfsException;
import com.mei.bnr.exception.BnrException;
import javafx.event.ActionEvent;
import org.tinylog.Logger;

public class MoneyManagementBnrUnloadController  {
    private final Agent agent;
    private final SceneManager sceneManager;
    private final ListenBnrEvent listenBnrEvent;
    public MoneyManagementBnrUnloadController(Agent agent, SceneManager sceneManager) {
        this.sceneManager=sceneManager;
        this.agent=agent;
        listenBnrEvent=new ListenBnrEvent(this,agent.getFinanceOperationRepository(),agent.getShift().getShiftId());
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
        FinanceOperationRepository financeOperationRepository;
        String shiftId;
        public ListenBnrEvent(MoneyManagementBnrUnloadController pduController, FinanceOperationRepository financeOperationRepository, String shiftId){
            this.controller=pduController;
            this.financeOperationRepository=financeOperationRepository;
            this.shiftId=shiftId;
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
                    try {
                        financeOperationRepository.markEmpty(shiftId);
                        BNRIntegration.bnrSetDepositZero();
                        Logger.tag(LoggerTag.BUSS).info("BNR Cashbox content set to zero for shift: {}", shiftId);
                        //TODO: Generate EOD report
                    } catch (BnrException e) {
                        Logger.tag(LoggerTag.APP).error("Error in set deposit to zero: {}", e.getMessage());
                    }
                    // remove from DB
                }

            }
        }
    }


}
