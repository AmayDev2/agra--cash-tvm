package com.amay.tvm.coin.service;

import com.amay.tvm.coin.model.AmountDetail;
import com.amay.tvm.coin.model.HaveAmountObject;
import com.amay.tvm.coin.model.ReturnableAmountObject;
import com.amay.tvm.coin.service.MaxChangePossibleService;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class MaxChangePossibleServiceTest {

    @Test
    void testOptimizeForSingleHoper_SuccessfulMatch() {
        MaxChangePossibleService service = new MaxChangePossibleService();
        ArrayList<AmountDetail> haveDetails = new ArrayList<>();
        haveDetails.add(new AmountDetail(10, 100, 10)); // 10 coins of 10
        HaveAmountObject haveAmount = new HaveAmountObject(haveDetails);

        ReturnableAmountObject returnableAmount = new ReturnableAmountObject(new ArrayList<>());
        returnableAmount.setTotalAmount(30); // Want 30

        service.optimizeForSingleHoper(returnableAmount, haveAmount);

        assertEquals(1, returnableAmount.getAmountDetailList().size());
        AmountDetail detail = returnableAmount.getAmountDetailList().get(0);
        assertEquals(10, detail.getAmount());
        assertEquals(3, detail.getQuantity());
        assertEquals(30, detail.getTotalAmount());
    }

    @Test
    void testOptimizeForSingleHoper_NoMatch() {
        MaxChangePossibleService service = new MaxChangePossibleService();
        ArrayList<AmountDetail> haveDetails = new ArrayList<>();
        haveDetails.add(new AmountDetail(5, 25, 5)); // 5 coins of 5
        HaveAmountObject haveAmount = new HaveAmountObject(haveDetails);

        ReturnableAmountObject returnableAmount = new ReturnableAmountObject(new ArrayList<>());
        returnableAmount.setTotalAmount(30); // Want 30, not possible with 5s

        service.optimizeForSingleHoper(returnableAmount, haveAmount);

        assertTrue(returnableAmount.getAmountDetailList().isEmpty());
    }

    @Test
    void testOptimizeForSingleHoper_AmountNotEnough_DataUnchanged() {
        MaxChangePossibleService service = new MaxChangePossibleService();
        ArrayList<AmountDetail> haveDetails = new ArrayList<>();
        haveDetails.add(new AmountDetail(10, 10, 1));
        haveDetails.add(new AmountDetail(5, 10, 2));
        HaveAmountObject haveAmount = new HaveAmountObject(haveDetails);

        ArrayList<AmountDetail> returnableDetails = new ArrayList<>();
        returnableDetails.add(new AmountDetail(5, 15, 3));
        returnableDetails.add(new AmountDetail(10, 10, 1));
        ReturnableAmountObject returnableAmount = new ReturnableAmountObject(new ArrayList<>(returnableDetails));
        returnableAmount.setTotalAmount(25);

        // Make a copy of the original list for comparison
        ArrayList<AmountDetail> originalDetails = new ArrayList<>();
        for (AmountDetail ad : returnableAmount.getAmountDetailList()) {
            originalDetails.add(new AmountDetail(ad.getAmount(), ad.getTotalAmount(), ad.getQuantity()));
        }

        service.optimizeForSingleHoper(returnableAmount, haveAmount);

        assertEquals(originalDetails.size(), returnableAmount.getAmountDetailList().size());
        for (int i = 0; i < originalDetails.size(); i++) {
            AmountDetail expected = originalDetails.get(i);
            AmountDetail actual = returnableAmount.getAmountDetailList().get(i);
            assertEquals(expected.getAmount(), actual.getAmount());
            assertEquals(expected.getTotalAmount(), actual.getTotalAmount());
            assertEquals(expected.getQuantity(), actual.getQuantity());
        }
    }

    // Java
    @Test
    void testOptimizeForSingleHoper_MultipleDenominations_OnlyOneValid() {
        MaxChangePossibleService service = new MaxChangePossibleService();
        ArrayList<AmountDetail> haveDetails = new ArrayList<>();
        haveDetails.add(new AmountDetail(10, 100, 10));
        haveDetails.add(new AmountDetail(20, 40, 2));
        HaveAmountObject haveAmount = new HaveAmountObject(haveDetails);

        ReturnableAmountObject returnableAmount = new ReturnableAmountObject(new ArrayList<>());
        returnableAmount.setTotalAmount(40);

        service.optimizeForSingleHoper(returnableAmount, haveAmount);

        assertEquals(1, returnableAmount.getAmountDetailList().size());
        AmountDetail detail = returnableAmount.getAmountDetailList().get(0);
        assertEquals(20, detail.getAmount());
        assertEquals(2, detail.getQuantity());
        assertEquals(40, detail.getTotalAmount());
    }

    @Test
    void testOptimizeForSingleHoper_ExactMatchWithSingleCoin() {
        MaxChangePossibleService service = new MaxChangePossibleService();
        ArrayList<AmountDetail> haveDetails = new ArrayList<>();
        haveDetails.add(new AmountDetail(50, 50, 1));
        HaveAmountObject haveAmount = new HaveAmountObject(haveDetails);

        ReturnableAmountObject returnableAmount = new ReturnableAmountObject(new ArrayList<>());
        returnableAmount.setTotalAmount(50);

        service.optimizeForSingleHoper(returnableAmount, haveAmount);

        assertEquals(1, returnableAmount.getAmountDetailList().size());
        AmountDetail detail = returnableAmount.getAmountDetailList().get(0);
        assertEquals(50, detail.getAmount());
        assertEquals(1, detail.getQuantity());
        assertEquals(50, detail.getTotalAmount());
    }

    @Test
    void testOptimizeForSingleHoper_ZeroTotalAmount() {
        MaxChangePossibleService service = new MaxChangePossibleService();
        ArrayList<AmountDetail> haveDetails = new ArrayList<>();
        haveDetails.add(new AmountDetail(10, 100, 10));
        HaveAmountObject haveAmount = new HaveAmountObject(haveDetails);

        ReturnableAmountObject returnableAmount = new ReturnableAmountObject(new ArrayList<>());
        returnableAmount.setTotalAmount(0);

        service.optimizeForSingleHoper(returnableAmount, haveAmount);

        assertTrue(returnableAmount.getAmountDetailList().isEmpty());
    }

}
