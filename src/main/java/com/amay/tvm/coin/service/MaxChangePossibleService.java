package com.amay.tvm.coin.service;


import com.amay.tvm.coin.model.AmountDetail;
import com.amay.tvm.coin.model.HaveAmountObject;
import com.amay.tvm.coin.model.ReturnableAmountObject;

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
        if (haveAmountObject.totalAmount < totalReturnableAmount) {
            // Return maximum possible with available amount
            return getReturnableAmount(returnableAmountObject, haveAmountObject, haveAmountObject.totalAmount, index);
        }

        AmountDetail currentDenomination = haveAmountObject.getArrayOfAmountDetails()[index];

        // Calculate maximum quantity we can use of current denomination
        int maxPossibleQuantity = Math.min(
                totalReturnableAmount / currentDenomination.amount,  // Based on amount needed
                currentDenomination.quantity                         // Based on availability
        );

        if (maxPossibleQuantity > 0) {
            // Use maximum possible of current denomination
            int amountUsed = currentDenomination.amount * maxPossibleQuantity;
            returnableAmountObject.amountDetailList.add(
                    new AmountDetail(currentDenomination.amount, amountUsed, maxPossibleQuantity)
            );
            returnableAmountObject.totalAmount += amountUsed;
            totalReturnableAmount -= amountUsed;
        }

        // Continue with remaining amount and next denomination
        return getReturnableAmount(returnableAmountObject, haveAmountObject, totalReturnableAmount, index + 1);
    }
}
