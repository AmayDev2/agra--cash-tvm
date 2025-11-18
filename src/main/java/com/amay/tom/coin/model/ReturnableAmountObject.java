package com.amay.tom.coin.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReturnableAmountObject{
    public List<AmountDetail> amountDetailList;
    public int totalAmount;

    public  ReturnableAmountObject(ArrayList<AmountDetail> amountDetailList) {
        this.amountDetailList=amountDetailList;
    }
}

