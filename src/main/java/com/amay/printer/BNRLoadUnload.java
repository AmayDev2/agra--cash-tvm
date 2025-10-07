package com.amay.printer;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BNRLoadUnload {

    private String reportType;

    private String stationName;
    private String shiftId;
    private String startTime;
    private String endTime;
    private String equipmentId;
    private String operatorId;

    private int rs10Count;
    private int rs10Amount;
    private int rs20Count;
    private int rs20Amount;
    private int rs50Count;
    private int rs50Amount;
    private int rs100Count;
    private int rs100Amount;
    private int rs200Count;
    private int rs200Amount;
    private int rs500Count;
    private int rs500Amount;
    private int bankTotalCount;
    private int bankTotalAmount;


}
