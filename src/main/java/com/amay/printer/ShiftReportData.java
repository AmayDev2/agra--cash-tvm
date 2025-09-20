package com.amay.printer;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class ShiftReportData {
    // Basic Info
    private String stationName;
    private String shiftId;
    private String startTime;
    private String endTime;
    private String equipmentId;
    private String operatorId;

    // Start Balance
    private String impressMoney;

    // QR Transaction counts and amounts
    private String sjtCashCount;
    private String sjtCashAmount;
    private String rjtCashCount;
    private String rjtCashAmount;
    private String gtCashCount;
    private String gtCashAmount;
    private String sjtUpiCount;
    private String sjtUpiAmount;
    private String rjtUpiCount;
    private String rjtUpiAmount;
    private String gtUpiCount;
    private String gtUpiAmount;
    private String sjtPosCount;
    private String sjtPosAmount;
    private String rjtPosCount;
    private String rjtPosAmount;
    private String gtPosCount;
    private String gtPosAmount;
    private String qrTotalCount;
    private String qrTotalAmount;

    // NCMC transaction counts and amounts
    private String ncmcCashCount;
    private String ncmcCashAmount;
    private String ncmcUpiCount;
    private String ncmcUpiAmount;
    private String ncmcPosCount;
    private String ncmcPosAmount;
    private String ncmcTotalCount;
    private String ncmcTotalAmount;

    // Bank note counts and amounts for each denomination
    private String rs10Count;
    private String rs10Amount;
    private String rs20Count;
    private String rs20Amount;
    private String rs50Count;
    private String rs50Amount;
    private String rs100Count;
    private String rs100Amount;
    private String rs200Count;
    private String rs200Amount;
    private String rs500Count;
    private String rs500Amount;
    private String bankTotalCount;
    private String bankTotalAmount;

    // Coin counts and amounts
    private String hopper1Count;
    private String hopper1Amount;
    private String hopper2Count;
    private String hopper2Amount;
    private String hopper3Count;
    private String hopper3Amount;
    private String coinTotalCount;
    private String coinTotalAmount;

    // Shift summary
    private String totalCashSales;
    private String totalUpiSales;
    private String totalPosSales;
    private String totalRevenue;
    private String availableCash;
    private String printTime;


}
