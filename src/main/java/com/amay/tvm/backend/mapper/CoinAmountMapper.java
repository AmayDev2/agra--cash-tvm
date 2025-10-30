package com.amay.tvm.backend.mapper;

import com.amay.tvm.backend.entity.AmountSnapShotEntity;
import com.amay.tvm.backend.entity.CoinAmountEntity;
import org.checkerframework.checker.units.qual.A;

public class CoinAmountMapper {
    public static AmountSnapShotEntity toSnapshot(CoinAmountEntity coinAmountEntity, String shiftId) {
        AmountSnapShotEntity amountSnapShotEntity = new AmountSnapShotEntity();

        amountSnapShotEntity.setUnitAmount(Integer.parseInt(coinAmountEntity.getContainerId()));
        amountSnapShotEntity.setCurrentQuantity(coinAmountEntity.getQuantity());
        amountSnapShotEntity.setShiftId(shiftId);
        amountSnapShotEntity.setContainerId(coinAmountEntity.getContainerId());

        return amountSnapShotEntity;
    }
}
