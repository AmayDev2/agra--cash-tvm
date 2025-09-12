package com.amay.tvm.coin.model;

import java.util.ArrayList;
import java.util.List;

public class HaveAmountObject{
    public List<AmountDetail> amountDetailList;
    public HaveAmountObject() {
        this.amountDetailList=new ArrayList<>();
    }
    public HaveAmountObject(List<AmountDetail> amountDetailList){
        this.amountDetailList=amountDetailList;
        totalAmount=amountDetailList.stream()
                .mapToInt(amountDetail -> amountDetail.getAmount()*amountDetail.getQuantity())
                .sum();

    }
    public AmountDetail[] getArrayOfAmountDetails(){
        return amountDetailList.toArray(AmountDetail[]::new);
    }
    public int totalAmount;
}