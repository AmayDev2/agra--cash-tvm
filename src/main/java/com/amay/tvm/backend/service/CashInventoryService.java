package com.amay.tvm.backend.service;

import com.amay.tom.agent.Agent;
import com.amay.tvm.backend.dto.CashInventoryDto;
import com.amay.tvm.backend.entity.AmountSnapShotEntity;
import com.amay.tvm.backend.repository.AmountSnapShotRepository;

import java.util.List;

public class CashInventoryService {

    private AmountSnapShotRepository amountSnapShotRepository;

    public CashInventoryService(AmountSnapShotRepository amountSnapShotRepository) {
        this.amountSnapShotRepository = amountSnapShotRepository;
    }

    public List<AmountSnapShotEntity> getCashInventoryByShiftId(String shiftId){
        return amountSnapShotRepository.findAllByShiftId(shiftId);
    }
}
