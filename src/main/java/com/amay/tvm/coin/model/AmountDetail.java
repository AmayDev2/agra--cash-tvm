package com.amay.tvm.coin.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AmountDetail{
    public int amount;
    public int totalAmount;
    public int quantity;

    public AmountDetail(int amount, int totalAmount, int quantity) {
        this.amount=amount;
        this.totalAmount=totalAmount;
        this.quantity=quantity;
    }
//    String currency;
//    String nodeId;
}