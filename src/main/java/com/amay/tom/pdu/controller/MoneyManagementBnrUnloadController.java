package com.amay.tom.pdu.controller;

import com.amay.printer.BNRLoadUnload;
import com.amay.printer.BalanceReport;
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
import com.amay.tvm.coin.service.HoppersRegistry;
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


    private void printUnloadReport() {
        NoteAmountDTO rs10= NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(10));
        NoteAmountDTO rs20=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(20));
        NoteAmountDTO rs50=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(50));
        NoteAmountDTO rs100=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(100));
        NoteAmountDTO rs200=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(200));
        NoteAmountDTO rs500=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(500));

        int totalQuantity= rs10.getCashInQuantity()+rs20.getCashInQuantity()+rs50.getCashInQuantity()
                +rs100.getCashInQuantity()+rs200.getCashInQuantity()+rs500.getCashInQuantity();

        int totalAmount= rs10.getCashInQuantity()*10+rs20.getCashInQuantity()*20+rs50.getCashInQuantity()*50
                +rs100.getCashInQuantity()*100+rs200.getCashInQuantity()*200+rs500.getCashInQuantity()*500;

        PrinterCommandDispatcher.INSTANCE.printBNRLoadUnload(
                BNRLoadUnload.builder()
                        .reportType("Unload BNR Report")
                        .stationName(SystemConfig.getInstance().getCurrentStation().getStationName())
                        .shiftId(agent.getShiftMaintenance().getShiftId())
                        .startTime(TimeUtil.formated(agent.getShiftMaintenance().getStartTime()))
                        .endTime("-")
                        .equipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                        .operatorId(agent.getShiftMaintenance().getOperatorId())
                        .rs10Count(rs10.getCashInQuantity())
                        .rs10Amount(rs10.getCashInQuantity()*10)
                        .rs20Count(rs20.getCashInQuantity())
                        .rs20Amount(rs20.getCashInQuantity()*20)
                        .rs50Count(rs50.getCashInQuantity())
                        .rs50Amount(rs50.getCashInQuantity()*50)
                        .rs100Count(rs100.getCashInQuantity())
                        .rs100Amount(rs100.getCashInQuantity()*100)
                        .rs200Count(rs200.getCashInQuantity())
                        .rs200Amount(rs200.getCashInQuantity()*200)
                        .rs500Count(rs500.getCashInQuantity())
                        .rs500Amount(rs500.getCashInQuantity()*500)
                        .bankTotalCount(totalQuantity)
                        .bankTotalAmount(totalAmount)
//                        .hopper1Count(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("1")))
//                        .hopper2Count(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("2")))
//                        .hopper3Count(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("3")))
//                        .hopper1Amount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("1"))*5)
//                        .hopper2Amount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("2"))*10)
//                        .hopper3Amount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("3"))*10)
//                        .coinTotalCount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("1"))+
//                                Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("2"))+
//                                Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("3")))
//                        .coinTotalAmount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("1"))*5+
//                                Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("2"))*10+
//                                Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("3"))*10)
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
