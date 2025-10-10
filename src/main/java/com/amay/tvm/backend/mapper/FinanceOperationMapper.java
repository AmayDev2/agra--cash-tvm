package com.amay.tvm.backend.mapper;

import com.amay.printer.BNRLoadUnload;
import com.amay.printer.CoinLoadedReport;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.session.Shift;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.enums.FinanceOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinanceOperationMapper {
    public static BNRLoadUnload toBNRLoadUnload(Agent agent,FinanceOperation financeOperation, List<FinanceOperationEntity> financeOperationEntities){
        if(!financeOperation.equals(FinanceOperation.BNR_LOAD) && !financeOperation.equals(FinanceOperation.BNR_UNLOAD)){
            return null;
        }

        Map<Integer,Integer> unitTypeQuantityMap = new HashMap<>();

        Shift shift = agent.getShift();
        SystemConfig systemConfig = agent.getSystemConfig();
        BNRLoadUnload.BNRLoadUnloadBuilder bnrLoadUnload = BNRLoadUnload.builder()
                .reportType(financeOperation.name())
                .shiftId(shift.getShiftId())
                .equipmentId(systemConfig.getCurrentEquipment().getEquipmentId())
                .operatorId(systemConfig.getCurrentUser().getUserId())
                .stationName(systemConfig.getCurrentStation().getStationName())
                .startTime(shift.getStartTime().toString())
                .endTime(shift.getEndTime().toString());

        for(FinanceOperationEntity financeOperationEntity : financeOperationEntities){
            int unitType = financeOperationEntity.getUnitAmount();
            int quantity = financeOperationEntity.getQuantity();
            unitTypeQuantityMap.put(unitType,quantity);
        }

        int rs10Count = unitTypeQuantityMap.getOrDefault(10, 0);
        int rs20Count = unitTypeQuantityMap.getOrDefault(20, 0);
        int rs50Count = unitTypeQuantityMap.getOrDefault(50, 0);
        int rs100Count = unitTypeQuantityMap.getOrDefault(100, 0);
        int rs200Count = unitTypeQuantityMap.getOrDefault(200, 0);
        int rs500Count = unitTypeQuantityMap.getOrDefault(500, 0);

        int rs10Amount = rs10Count * 10;
        int rs20Amount = rs20Count * 20;
        int rs50Amount = rs50Count * 50;
        int rs100Amount = rs100Count * 100;
        int rs200Amount = rs200Count * 200;
        int rs500Amount = rs500Count * 500;

        int totalCount = rs10Count + rs20Count + rs50Count + rs100Count + rs200Count + rs500Count;
        int totalAmount = rs10Amount + rs20Amount + rs50Amount + rs100Amount + rs200Amount + rs500Amount;

        bnrLoadUnload
                .rs10Count(rs10Count)
                .rs10Amount(rs10Amount)
                .rs20Count(rs20Count)
                .rs20Amount(rs20Amount)
                .rs50Count(rs50Count)
                .rs50Amount(rs50Amount)
                .rs100Count(rs100Count)
                .rs100Amount(rs100Amount)
                .rs200Count(rs200Count)
                .rs200Amount(rs200Amount)
                .rs500Count(rs500Count)
                .rs500Amount(rs500Amount)
                .bankTotalCount(totalCount)
                .bankTotalAmount(totalAmount);

        return bnrLoadUnload.build();
    }

    public static CoinLoadedReport toCoinLoadedReport(Agent agent,FinanceOperation financeOperation, List<FinanceOperationEntity> financeOperationEntities) {
        if(!financeOperation.equals(FinanceOperation.COIN_LOAD) && !financeOperation.equals(FinanceOperation.COIN_UNLOAD)){
            return null;
        }
        Map<Integer, Integer> unitTypeQuantityMap = new HashMap<>();

        Shift shift = agent.getShift();
        SystemConfig systemConfig = agent.getSystemConfig();

        CoinLoadedReport.CoinLoadedReportBuilder coinLoadedReport = CoinLoadedReport.builder()
                .reportType(financeOperation.name())
                .shiftId(shift.getShiftId())
                .equipmentId(systemConfig.getCurrentEquipment().getEquipmentId())
                .operatorId(systemConfig.getCurrentUser().getUserId())
                .stationName(systemConfig.getCurrentStation().getStationName())
                .startTime(shift.getStartTime().toString())
                .endTime(shift.getEndTime().toString());

        for (FinanceOperationEntity entity : financeOperationEntities) {
            int unitType = entity.getUnitAmount();
            int quantity = entity.getQuantity();
            unitTypeQuantityMap.put(unitType, quantity);
        }

        int hopper1Count = unitTypeQuantityMap.getOrDefault(1, 0);
        int hopper1Amount = hopper1Count * 5;

        int hopper2Count = unitTypeQuantityMap.getOrDefault(2, 0);
        int hopper2Amount = hopper2Count * 10;

        int hopper3Count = unitTypeQuantityMap.getOrDefault(3, 0);
        int hopper3Amount = hopper3Count * 10;

        int coinTotalCount = hopper1Count + hopper2Count + hopper3Count;
        int coinTotalAmount = hopper1Amount + hopper2Amount + hopper3Amount;

        coinLoadedReport
                .hopper1Count(hopper1Count)
                .hopper1Amount(hopper1Amount)
                .hopper2Count(hopper2Count)
                .hopper2Amount(hopper2Amount)
                .hopper3Count(hopper3Count)
                .hopper3Amount(hopper3Amount)
                .coinTotalCount(coinTotalCount)
                .coinTotalAmount(coinTotalAmount);

        return coinLoadedReport.build();
    }

}
