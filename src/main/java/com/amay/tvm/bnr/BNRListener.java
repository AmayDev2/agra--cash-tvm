package com.amay.tvm.bnr;

import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.enums.FinanceOperation;
import com.amay.tvm.backend.repository.FinanceOperationRepository;
import com.amay.tvm.backend.repository.NoteAmountRepository;
import com.amay.tvm.controller.CashInsertProcessingController;
import javafx.application.Platform;

import java.util.List;

public class BNRListener implements IBNRListener {
    private final CashInsertProcessingController controller;
    private final double RUPEYA_MULTIPLAYER=0.01;
    private final FinanceOperationRepository financeOperationRepository;
    private final NoteAmountRepository noteAmountRepository;
    private final String shiftId;

    public BNRListener(CashInsertProcessingController controller,
                       FinanceOperationRepository financeOperationRepository,
                       String shiftId,
                       NoteAmountRepository noteAmountRepository) {
        this.controller = controller;
        this.financeOperationRepository = financeOperationRepository;
        this.noteAmountRepository = noteAmountRepository;
        this.shiftId = shiftId;
    }

    @Override
    public void acceptedAmount(int acceptedAmount) {
        financeOperationRepository.upsert(new FinanceOperationEntity()
                .setUnitAmount((int) (RUPEYA_MULTIPLAYER* acceptedAmount))
                .setOperationType(FinanceOperation.BNR_NOT_COMMITTED)
                .setQuantity(1)
                .setShiftId(shiftId));
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
        switch (status){
            case FAILED ->   financeOperationRepository.rollback();
            case SUCCESS -> financeOperationRepository.markCommited();
        }
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
        amountAndQuantity.forEach(x->x.setShiftId(shiftId));
        financeOperationRepository.upsert(amountAndQuantity);

    }
}
