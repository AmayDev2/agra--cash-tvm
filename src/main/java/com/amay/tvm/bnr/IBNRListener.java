package com.amay.tvm.bnr;

import java.util.List;

public interface IBNRListener {
    void acceptedAmount(int acceptedAmount);
    void informationToShow(String message);
    void compareTotalAmountAndChange(int totalAcceptedAmount,int change);
    void setStatus(BNRStatus status);
    void setAllowedNotes(List<Integer> list);
}
