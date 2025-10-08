package com.amay.tvm.bnr;

import com.amay.tvm.backend.entity.FinanceOperationEntity;

import java.util.List;

public interface IBNRListener {
    void acceptedAmount(int acceptedAmount);
    void informationToShow(String message);
    void compareTotalAmountAndChange(int totalAcceptedAmount,int change);
    void setStatus(BNRStatus status);
    void setAllowedNotes(List<Integer> list);

    void disableCancelButton();

    void getAcceptedAmount();

    void dispensedAmount(List<FinanceOperationEntity> amountAndQuantity);
}
