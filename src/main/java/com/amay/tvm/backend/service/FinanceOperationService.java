package com.amay.tvm.backend.service;

import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.repository.FinanceOperationRepository;
import java.util.List;

public class FinanceOperationService {

    private final FinanceOperationRepository financeOperationRepository;

    public FinanceOperationService(FinanceOperationRepository financeOperationRepository) {
        this.financeOperationRepository = financeOperationRepository;
    }

    public List<FinanceOperationEntity> getFinanceOperationEntityLoadUnloadListByShiftId(String shiftId){
        return financeOperationRepository.getLoadUnloadOperationByShiftId(shiftId);
    }
}
