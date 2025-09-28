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
    private int impressMoney;

    // QR Transaction counts and amounts
    private int sjtCashCount;
    private int sjtCashAmount;
    private int rjtCashCount;
    private int rjtCashAmount;
    private int gtCashCount;
    private int gtCashAmount;
    private int sjtUpiCount;
    private int sjtUpiAmount;
    private int rjtUpiCount;
    private int rjtUpiAmount;
    private int gtUpiCount;
    private int gtUpiAmount;
    private int sjtPosCount;
    private int sjtPosAmount;
    private int rjtPosCount;
    private int rjtPosAmount;
    private int gtPosCount;
    private int gtPosAmount;
    private int qrTotalCount;
    private int qrTotalAmount;

    // NCMC transaction counts and amounts
    private int ncmcCashCount;
    private int ncmcCashAmount;
    private int ncmcUpiCount;
    private int ncmcUpiAmount;
    private int ncmcPosCount;
    private int ncmcPosAmount;
    private int ncmcTotalCount;
    private int ncmcTotalAmount;

    // Bank note counts and amounts for each denomination
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

    // Coin counts and amounts
    private int hopper1Count;
    private int hopper1Amount;
    private int hopper2Count;
    private int hopper2Amount;
    private int hopper3Count;
    private int hopper3Amount;
    private int coinTotalCount;
    private int coinTotalAmount;

    // Shift summary
    private int totalCashSales;
    private int totalUpiSales;
    private int totalPosSales;
    private int totalRevenue;
    private int availableCash;
    private String printTime;


}
