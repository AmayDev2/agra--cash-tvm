package com.amay.tvm.backend.mapper;

import com.amay.tvm.backend.entity.AmountSnapShotEntity;
import com.amay.tvm.backend.entity.CoinAmountEntity;
import com.amay.tvm.backend.entity.NoteAmountEntity;

public class CoinAmountMapper {
    public static AmountSnapShotEntity toSnapshot(CoinAmountEntity coinAmountEntity, String shiftId) {
        AmountSnapShotEntity amountSnapShotEntity = new AmountSnapShotEntity();
        amountSnapShotEntity.setContainerId("CASH");
        amountSnapShotEntity.setShiftId(shiftId);
        amountSnapShotEntity.setContainerId(coinAmountEntity.getContainerId());
        amountSnapShotEntity.setUnitAmount(coinAmountEntity.getUnitAmount());
        amountSnapShotEntity.setCurrentQuantity(coinAmountEntity.getQuantity());
        return amountSnapShotEntity;

    }
}
