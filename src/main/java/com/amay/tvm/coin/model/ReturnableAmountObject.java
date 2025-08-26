package com.amay.tvm.coin.model;

import java.util.ArrayList;
import java.util.List;

public class ReturnableAmountObject{
    public List<AmountDetail> amountDetailList;
    public int totalAmount;

    public  ReturnableAmountObject(ArrayList<AmountDetail> amountDetailList) {
        this.amountDetailList=amountDetailList;
    }
}

