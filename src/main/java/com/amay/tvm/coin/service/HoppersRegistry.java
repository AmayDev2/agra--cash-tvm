package com.amay.tvm.coin.service;

import com.amay.tvm.coin.model.AmountDetail;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public enum HoppersRegistry {
    INSTANCE;
    @Getter
    private List<AmountDetail> hoppers;
    public void setHoppers(int hop1, int hop2, int hop3){
        hoppers=new ArrayList<>();
        hoppers.add(new AmountDetail(5,5*hop1,hop1));
        hoppers.add(new AmountDetail(10,10*hop2,hop2));
        hoppers.add(new AmountDetail(10,10*hop3,hop3));
        hoppers.sort((hop11,hop22)->Integer.compare(hop22.amount,hop11.amount));
    }

    public void updateHopper(int hopper,int dispQuan) {
        hopper-=1;
        AmountDetail amountDetail=hoppers.get(hopper);
        amountDetail.quantity-=dispQuan;
    }
}
