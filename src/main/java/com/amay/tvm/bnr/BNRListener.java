package com.amay.tvm.bnr;

import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.controller.CashInsertProcessingController;
import javafx.application.Platform;

import java.util.List;

public class BNRListener implements IBNRListener {
    private final CashInsertProcessingController controller;
    private final double RUPEYA_MULTIPLAYER=0.01;

    public BNRListener(CashInsertProcessingController controller) {
        this.controller = controller;
    }

    @Override
    public void acceptedAmount(int acceptedAmount) {
        Platform.runLater(()-> {controller.setInsertedAmount((int) (acceptedAmount*RUPEYA_MULTIPLAYER));});
    }

    @Override
    public void informationToShow(String message) {
        Platform.runLater(()-> {controller.setOpsMessage(message);});
    }

    @Override
    public void compareTotalAmountAndChange(int totalAcceptedAmount, int change) {
        Platform.runLater(()-> {controller.compareAndSet((int)(totalAcceptedAmount*RUPEYA_MULTIPLAYER),(int)(change*RUPEYA_MULTIPLAYER));});

    }

    @Override
    public void setStatus(BNRStatus status) {

    }

    @Override
    public void setAllowedNotes(List<Integer> list) {
        controller.setAcceptableNote(list);
    }

    @Override
    public void disableCancelButton() {
        controller.disableCancelButton();
    }

    @Override
    public void getAcceptedAmount() {

    }

    @Override
    public void dispensedAmount(List<FinanceOperationEntity> amountAndQuantity) {

    }
}
