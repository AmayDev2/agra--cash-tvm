package com.amay.tvm.bnr;

import com.amay.tom.pdu.controller.MoneyManagementBnrLoadController;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.entity.NoteAmountEntity;
import com.amay.tvm.backend.enums.FinanceOperation;
import com.amay.tvm.backend.repository.FinanceOperationRepository;
import com.amay.tvm.backend.repository.NoteAmountRepository;
import com.amay.tvm.controller.CashInsertProcessingController;
import javafx.application.Platform;

import java.util.List;

public class BNRListenerLoad implements IBNRListener {
    private final MoneyManagementBnrLoadController controller;
    private final FinanceOperationRepository financeOperationRepository;
    private final NoteAmountRepository noteAmountRepository;
    private final String shiftId;


    public BNRListenerLoad(MoneyManagementBnrLoadController controller,
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
        double RUPERTA_MULTIPLAYER = 0.01;
        financeOperationRepository.upsert(new FinanceOperationEntity()
                .setUnitAmount((int) (RUPERTA_MULTIPLAYER * acceptedAmount))
                .setOperationType(FinanceOperation.BNR_NOT_COMMITTED)
                .setShiftId(shiftId));
    }

    @Override
    public void informationToShow(String message)
    {

    }

    @Override
    public void compareTotalAmountAndChange(int totalAcceptedAmount, int change) {

    }

    @Override
    public void setStatus(BNRStatus status) {
        switch (status){
            case SUCCESS -> financeOperationRepository.markCommited();
            case FAILED -> financeOperationRepository.rollback();
        }

    }

    @Override
    public void setAllowedNotes(List<Integer> list) {

    }

    @Override
    public void disableCancelButton() {
        Platform.runLater(controller::disableCancelButton);
    }

    @Override
    public void getAcceptedAmount() {

    }

    @Override
    public void dispensedAmount(List<FinanceOperationEntity> amountAndQuantity) {

    }
}
