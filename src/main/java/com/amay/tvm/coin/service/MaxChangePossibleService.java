package com.amay.tvm.coin.service;


import com.amay.tvm.coin.model.AmountDetail;
import com.amay.tvm.coin.model.HaveAmountObject;
import com.amay.tvm.coin.model.ReturnableAmountObject;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MaxChangePossibleService {

    public ReturnableAmountObject getReturnableAmount(ReturnableAmountObject returnableAmountObject,
                                                      HaveAmountObject haveAmountObject,
                                                      int totalReturnableAmount,
                                                      int index) {

        // Base cases
        if (totalReturnableAmount <= 0 || index >= haveAmountObject.amountDetailList.size()) {
            return returnableAmountObject;
        }

        // If we don't have enough total amount available
        if (haveAmountObject.getTotalAmount() < totalReturnableAmount) {
            // Return maximum possible with available amount
            return getReturnableAmount(returnableAmountObject, haveAmountObject, haveAmountObject.getTotalAmount(), index);
        }

        AmountDetail currentDenomination = haveAmountObject.getArrayOfAmountDetails()[index];

        // Calculate maximum quantity we can use of current denomination
        int maxPossibleQuantity = Math.min(
                totalReturnableAmount / currentDenomination.getAmount(),  // Based on amount needed
                currentDenomination.getQuantity()                        // Based on availability
        );

        if (maxPossibleQuantity > 0) {
            // Use maximum possible of current denomination
            int amountUsed = currentDenomination.getAmount() * maxPossibleQuantity;
            returnableAmountObject.amountDetailList.add(
                    new AmountDetail(currentDenomination.getAmount(), amountUsed, maxPossibleQuantity).setContainerId(currentDenomination.getContainerId())
            );
            returnableAmountObject.totalAmount += amountUsed;
            totalReturnableAmount -= amountUsed;
        }

        // Continue with remaining amount and next denomination
        return getReturnableAmount(returnableAmountObject, haveAmountObject, totalReturnableAmount, index + 1);
    }

    public void optimizeForSingleHoper(ReturnableAmountObject returnableAmount, HaveAmountObject haveAmount) {
        if(returnableAmount.getTotalAmount()<=0)return;
        Collections.reverse(haveAmount.amountDetailList);
        //check for availability
                haveAmount.amountDetailList.stream()
                .filter(amountDetail ->
                        amountDetail.getTotalAmount() >= returnableAmount.getTotalAmount() &&
                                returnableAmount.getTotalAmount() % amountDetail.getAmount() == 0
                )
                .findFirst().ifPresent(matched-> {
                    returnableAmount.getAmountDetailList().clear();
                    int amount=matched.getAmount();
                    int quantity=returnableAmount.getTotalAmount()/matched.getAmount();
                    returnableAmount.getAmountDetailList().add(new AmountDetail(amount,amount*quantity ,quantity).setContainerId(matched.getContainerId()));

                });

    }
}
