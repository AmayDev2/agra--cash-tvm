package com.amay.tvm.coin.model;

import jakarta.persistence.Access;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AmountDetail{
    public int amount;
    public int totalAmount;
    public int quantity;
    private String containerId;

    public AmountDetail(int amount, int totalAmount, int quantity) {
        this.amount=amount;
        this.totalAmount=amount*quantity;
        this.quantity=quantity;
    }

}