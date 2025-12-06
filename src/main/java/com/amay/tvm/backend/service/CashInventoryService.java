package com.amay.tvm.backend.service;

import com.amay.tom.model.tvmConfig.TvmConfig;
import com.amay.tvm.backend.entity.AmountSnapShotEntity;
import com.amay.tvm.backend.enums.ContainerId;
import com.amay.tvm.backend.repository.AmountSnapShotRepository;
import com.amay.utils.MapUtil;

import java.util.List;

public class CashInventoryService {

    private final AmountSnapShotRepository amountSnapShotRepository;
    private final TvmConfig tvmConfig;

    public CashInventoryService(AmountSnapShotRepository amountSnapShotRepository, TvmConfig tvmConfig) {
        this.amountSnapShotRepository = amountSnapShotRepository;
        this.tvmConfig = tvmConfig;
    }

    public List<AmountSnapShotEntity> getCashInventoryByShiftId(String shiftId){
        List<AmountSnapShotEntity> amountSnapShotEntityList = amountSnapShotRepository.findAllByShiftId(shiftId);
        amountSnapShotEntityList.forEach(amountSnapShotEntity -> amountSnapShotEntity.setUnitAmount(
                amountSnapShotEntity.getContainerId().equals(ContainerId.CM.name())
                        ? MapUtil.hopperIdToUnitAmount(amountSnapShotEntity.getUnitAmount(),tvmConfig)
                        : amountSnapShotEntity.getUnitAmount()
        ));
        return amountSnapShotEntityList;
    }
}
