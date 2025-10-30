package com.amay.tom.pdu.controller;

import com.amay.printer.BNRLoadUnload;
import com.amay.printer.PrinterCommandDispatcher;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.service.devices.DeviceStatusListener;
import com.amay.tom.utils.time.TimeUtil;
import com.amay.tvm.backend.dto.NoteAmountDTO;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.mapper.NoteAmountMapper;
import com.amay.tvm.backend.repository.FinanceOperationRepository;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.coin.model.HaveAmountObject;
import com.jxfs.events.JxfsException;
import com.mei.bnr.exception.BnrException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.tinylog.Logger;

public class MoneyManagementBnrUnloadController  {
    private final Agent agent;
    private final SceneManager sceneManager;
    private final ListenBnrEvent listenBnrEvent;
    @FXML private Label totalAmount;
    private HaveAmountObject haveAmountObject=null;
    public MoneyManagementBnrUnloadController(Agent agent, SceneManager sceneManager) {
        this.sceneManager=sceneManager;
        this.agent=agent;
        listenBnrEvent=new ListenBnrEvent(this,agent.getFinanceOperationRepository(),agent.getShiftMaintenance().getShiftId());
        agent.getPeripheralMonitor().addDeviceStatusListener(listenBnrEvent);
        haveAmountObject=getAmount();
    }

    private HaveAmountObject getAmount() {
        HaveAmountObject haveAmountObject;
        try {
            haveAmountObject=BNRIntegration.getBnrHaveAmountObject();
        } catch (JxfsException e) {
            throw new RuntimeException(e);
        }
        return haveAmountObject;
    }

    @FXML void initialize() {
        totalAmount.setText(String.valueOf(haveAmountObject.getTotalAmount()));
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


    private void printUnloadReport() {
        NoteAmountDTO rs10= NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(10));
        NoteAmountDTO rs20=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(20));
        NoteAmountDTO rs50=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(50));
        NoteAmountDTO rs100=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(100));
        NoteAmountDTO rs200=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(200));
        NoteAmountDTO rs500=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(500));

        int totalQuantity=
                 rs10.getCurrentQuantity()
                +rs20.getCurrentQuantity()
                +rs50.getCurrentQuantity()
                +rs100.getCurrentQuantity()
                +rs200.getCurrentQuantity()
                +rs500.getCurrentQuantity();

        int totalAmount=
                         rs10.getCurrentQuantity()*rs10.getUnitAmount()
                        +rs20.getCurrentQuantity()*rs20.getUnitAmount()
                        +rs50.getCurrentQuantity()*rs50.getUnitAmount()
                        +rs100.getCurrentQuantity()*rs100.getUnitAmount()
                        +rs200.getCurrentQuantity()*rs200.getUnitAmount()
                        +rs500.getCurrentQuantity()*rs500.getUnitAmount();

        PrinterCommandDispatcher.INSTANCE.printBNRLoadUnload(
                BNRLoadUnload.builder()
                        .reportType("Unload BNR Report")
                        .stationName(SystemConfig.getInstance().getCurrentStation().getStationName())
                        .shiftId(agent.getShiftMaintenance().getShiftId())
                        .startTime(TimeUtil.formated(agent.getShiftMaintenance().getStartTime()))
                        .endTime("-")
                        .equipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                        .operatorId(agent.getShiftMaintenance().getOperatorId())
                        .rs10Count(rs10.getCurrentQuantity())
                        .rs10Amount(rs10.getCurrentQuantity()*rs10.getUnitAmount())
                        .rs20Count(rs20.getCurrentQuantity())
                        .rs20Amount(rs20.getCurrentQuantity()*rs20.getUnitAmount())
                        .rs50Count(rs50.getCurrentQuantity())
                        .rs50Amount(rs50.getCurrentQuantity()*rs50.getUnitAmount())
                        .rs100Count(rs100.getCurrentQuantity())
                        .rs100Amount(rs100.getCurrentQuantity()*rs100.getUnitAmount())
                        .rs200Count(rs200.getCurrentQuantity())
                        .rs200Amount(rs200.getCurrentQuantity()*rs200.getUnitAmount())
                        .rs500Count(rs500.getCurrentQuantity())
                        .rs500Amount(rs500.getCurrentQuantity()*rs500.getUnitAmount())
                        .bankTotalCount(totalQuantity)
                        .bankTotalAmount(totalAmount)
                        .build()
        );

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
                        //TODO: Generate EOD report
                        this.controller.printUnloadReport();
                        financeOperationRepository.markEmpty(shiftId);
                        BNRIntegration.bnrSetDepositZero();
                        Logger.tag(LoggerTag.BUSS).info("BNR Cashbox content set to zero for shift: {}", shiftId);

                    } catch (BnrException e) {
                        Logger.tag(LoggerTag.APP).error("Error in set deposit to zero: {}", e.getMessage());
                    }
                    // remove from DB
                }

            }
        }
    }


}
