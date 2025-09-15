package com.amay.tvm.coin.model;

import jakarta.persistence.Access;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AmountDetail{
    private int amount;
    private int totalAmount;
    private int quantity;
    private String containerId;

    public AmountDetail(int amount, int totalAmount, int quantity) {
        this.amount=amount;
        this.quantity=quantity;
        this.totalAmount=amount*quantity;
    }

    public int getTotalAmount(){
        return amount*quantity;
    }

    public void addQuantity(int dispensedQuantity) {
        this.quantity+=dispensedQuantity;
    }
}