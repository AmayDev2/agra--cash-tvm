package com.amay.printer;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CoinLoadedReport {

    private String reportType;

    private String stationName;
    private String shiftId;
    private String startTime;
    private String endTime;
    private String equipmentId;
    private String operatorId;

    private int hopper1Count;
    private int hopper1Amount;
    private int hopper2Count;
    private int hopper2Amount;
    private int hopper3Count;
    private int hopper3Amount;
    private int coinTotalCount;
    private int coinTotalAmount;


}
