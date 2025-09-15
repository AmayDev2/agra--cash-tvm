package com.amay.tvm.coin.service;

import com.amay.tvm.backend.entity.CoinAmountEntity;
import com.amay.tvm.backend.repository.CoinAmountRepository;
import com.amay.tvm.coin.model.AmountDetail;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public enum HoppersRegistry {
    INSTANCE;
    @Getter
    private List<AmountDetail> hoppers;
    public void setHoppers(int amountHop1, int amountHop2, int amountHop3, int hop1CoinQuantity, int hop2CoinQuantity, int hop3CoinQuantity, CoinAmountRepository coinAmountRepository){
        hoppers=new ArrayList<>();
        hoppers.add(new AmountDetail(amountHop1,amountHop1*hop1CoinQuantity,hop1CoinQuantity).setContainerId("1"));
        hoppers.add(new AmountDetail(amountHop2,amountHop2*hop2CoinQuantity,hop2CoinQuantity).setContainerId("2"));
        hoppers.add(new AmountDetail(amountHop3,amountHop3*hop3CoinQuantity,hop3CoinQuantity).setContainerId("3"));
        //        CoinAmountEntity coinAmountEntity=coinAmountRepository.findById("1");
//        hoppers.add(new AmountDetail(coinAmountEntity.getUnitAmount(),coinAmountEntity.getUnitAmount()*coinAmountEntity.getQuantity(),coinAmountEntity.getQuantity()).setContainerId(coinAmountEntity.getContainerId()));
//        coinAmountEntity=coinAmountRepository.findById("2");
//        hoppers.add(new AmountDetail(coinAmountEntity.getUnitAmount(),coinAmountEntity.getUnitAmount()*coinAmountEntity.getQuantity(),coinAmountEntity.getQuantity()).setContainerId(coinAmountEntity.getContainerId()));
//        coinAmountEntity=coinAmountRepository.findById("3");
//        hoppers.add(new AmountDetail(coinAmountEntity.getUnitAmount(),coinAmountEntity.getUnitAmount()*coinAmountEntity.getQuantity(),coinAmountEntity.getQuantity()).setContainerId(coinAmountEntity.getContainerId()));
        hoppers.sort((hop11, hop22)->Integer.compare(hop22.getAmount(),hop11.getAmount())); //DEC
    }

    public String getHopperQuantity(String id){
        return hoppers.stream().filter(hopper -> hopper.getContainerId().equals(id)).findFirst().map(hopper -> String.valueOf(hopper.getQuantity())).orElse("0");
    }

    public void resetHopper(int hopperId) {
        hoppers.stream().filter(hopper -> hopper.getContainerId().equals(String.valueOf(hopperId))).findFirst().ifPresent(hopper -> hopper.setQuantity(0));
    }

    public void updateHopper(int hopperId,int dispensedQuantity) {
        hoppers.stream().filter(hopper -> hopper.getContainerId().equals(String.valueOf(hopperId))).findFirst().ifPresent(hopper -> deductQuantity(hopper, dispensedQuantity));
    }
    public void updateHopperAdd(int hopperId,int dispensedQuantity) {
        hoppers.stream().filter(hopper -> hopper.getContainerId().equals(String.valueOf(hopperId))).findFirst().ifPresent(hopper -> addQuantity(hopper, dispensedQuantity));
    }

    private void addQuantity(AmountDetail hopper, int dispensedQuantity) {
        hopper.addQuantity(dispensedQuantity);
    }

    private void deductQuantity(AmountDetail hopper, int dispensedQuantity) {
        hopper.addQuantity(-dispensedQuantity);
    }

    public int getAmount(String number) {
        return hoppers.stream().filter(hopper -> hopper.getContainerId().equals(number)).findFirst().map(AmountDetail::getAmount).orElse(0);
    }

    public List<AmountDetail> getHopperOfMaxAmount() {
        return Collections.singletonList(hoppers.stream().max(Comparator.comparingInt(hopper -> hopper.getAmount() * hopper.getQuantity())).orElse((AmountDetail) List.of()));
    }
}
