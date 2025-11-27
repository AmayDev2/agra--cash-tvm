package com.amay.tvm.backend.mapper;

import com.amay.printer.BNRLoadUnload;
import com.amay.printer.CoinLoadedReport;
import com.amay.tvm.backend.entity.AmountSnapShotEntity;
import com.amay.tvm.backend.enums.ContainerId;
import com.amay.tvm.backend.enums.Hopper;
import com.amay.tvm.backend.enums.NoteDenomination;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmountSnapshotMapper {
    public static BNRLoadUnload toBNRLoadUnload(List<AmountSnapShotEntity> amountSnapShotEntityList){

        Map<NoteDenomination,Integer> noteCountMap=new HashMap<>();
        for(AmountSnapShotEntity amountSnapShotEntity : amountSnapShotEntityList){
            if(amountSnapShotEntity.getContainerId().equals(ContainerId.CB.name())){
                noteCountMap.put(NoteDenomination.fromValue(amountSnapShotEntity.getUnitAmount()),
                        noteCountMap.getOrDefault(amountSnapShotEntity.getCurrentQuantity(),0)
                                +amountSnapShotEntity.getCurrentQuantity());
            }
        }
        int rs10Count = noteCountMap.getOrDefault(NoteDenomination.NOTE_10, 0);
        int rs20Count = noteCountMap.getOrDefault(NoteDenomination.NOTE_20, 0);
        int rs50Count = noteCountMap.getOrDefault(NoteDenomination.NOTE_50, 0);
        int rs100Count = noteCountMap.getOrDefault(NoteDenomination.NOTE_100, 0);
        int rs200Count = noteCountMap.getOrDefault(NoteDenomination.NOTE_200, 0);
        int rs500Count = noteCountMap.getOrDefault(NoteDenomination.NOTE_500, 0);

        int rs10Amount = rs10Count * 10;
        int rs20Amount = rs20Count * 20;
        int rs50Amount = rs50Count * 50;
        int rs100Amount = rs100Count * 100;
        int rs200Amount = rs200Count * 200;
        int rs500Amount = rs500Count * 500;

        int totalCount = rs10Count + rs20Count + rs50Count + rs100Count + rs200Count + rs500Count;
        int totalAmount = rs10Amount + rs20Amount + rs50Amount + rs100Amount + rs200Amount + rs500Amount;

        return BNRLoadUnload.builder()
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
                .bankTotalAmount(totalAmount)
                .build();



    }
    public static CoinLoadedReport toCoinLoadUnload(List<AmountSnapShotEntity> amountSnapShotEntityList){
        Map<Hopper,Integer> coinAmount= new HashMap<>();
        amountSnapShotEntityList.stream()
                .filter(x -> x.getContainerId().equals(ContainerId.CM.name()))
                .peek(x -> x.setContainerId(String.valueOf(x.getUnitAmount()))) // update one property
                .peek(x -> x.setUnitAmount(Hopper.HOPPER1.getHopperByHopperId(x.getUnitAmount()).getUnitAmount())) // update one property
                .forEach(x -> coinAmount.put(
                        Hopper.HOPPER1.getHopperByHopperId(Integer.parseInt(x.getContainerId())),
                        x.getCurrentQuantity()
                ));

        int hopper1Count=coinAmount.getOrDefault(Hopper.HOPPER1, 0);
        int hopper2Count=coinAmount.getOrDefault(Hopper.HOPPER2, 0);
        int hopper3Count=coinAmount.getOrDefault(Hopper.HOPPER3, 0);


       int hopper1Amount=hopper1Count*Hopper.HOPPER1.getUnitAmount();
       int hopper2Amount=hopper2Count*Hopper.HOPPER2.getUnitAmount();
       int hopper3Amount=hopper3Count*Hopper.HOPPER3.getUnitAmount();

       return CoinLoadedReport.builder()
                .hopper1Count(hopper1Count)
                .hopper1Amount(hopper1Amount)
                .hopper2Count(hopper2Count)
                .hopper2Amount(hopper2Amount)
                .hopper3Count(hopper3Count)
                .hopper3Amount(hopper3Amount)
                .coinTotalCount(hopper1Count+hopper2Count+hopper3Count)
                .coinTotalAmount(hopper1Amount+hopper2Amount+hopper3Amount)
                .build();

    }
}
