package com.amay.tvm.backend.service;

import com.amay.tom.model.tvmConfig.TvmConfig;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.repository.FinanceOperationRepository;
import com.amay.utils.MapUtil;

import java.util.List;

public class FinanceOperationService {

    private final FinanceOperationRepository financeOperationRepository;
    private final TvmConfig tvmConfig;

    public FinanceOperationService(FinanceOperationRepository financeOperationRepository, TvmConfig tvmConfig) {
        this.financeOperationRepository = financeOperationRepository;
        this.tvmConfig = tvmConfig;
    }

    public List<FinanceOperationEntity> getFinanceOperationEntityLoadUnloadListByShiftId(String shiftId){
        List<FinanceOperationEntity> financeOperationEntityList =  financeOperationRepository.getLoadUnloadOperationByShiftId(shiftId);

        financeOperationEntityList.forEach(financeOperationEntity -> financeOperationEntity.setUnitAmount(
                        (financeOperationEntity.getOperationType().name().contains("COIN")
                                ? MapUtil.hopperIdToUnitAmount(financeOperationEntity.getUnitAmount(),tvmConfig)
                                : financeOperationEntity.getUnitAmount())));

        return financeOperationEntityList;
    }
}
