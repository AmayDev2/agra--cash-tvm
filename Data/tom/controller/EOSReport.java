package com.amay.tom.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.time.LocalDateTime;

public class EOSReport {
    @FXML private Text sjtCashCount;
    @FXML private Text sjtCashAmount;
    @FXML private Text rjtCashCount;
    @FXML private Text rjtCashAmount;
    @FXML private Text groupCashCount;
    @FXML private Text groupCashAmount;
    @FXML private Text ncmcCashCount;
    @FXML private Text ncmcCashAmount;
    @FXML private Text ncmcCashTopupAmount;
    @FXML private Text ncmcCashTopupCount;
    @FXML private Text freeExitCount;
    @FXML private Text freeExitAmount;
    @FXML private Text paidExitCount;
    @FXML private Text paidExitAmount;
    @FXML private Text duplicateTicketCount;
    @FXML private Text duplicateTicketAmount;
    @FXML private Text totalAdminCount;
    @FXML private Text totalAdminAmount;
    @FXML private Text adjustmentCount;
    @FXML private Text adjustmentAmount;
    @FXML private Text totalAdjustmentCount;
    @FXML private Text totalAdjustmentAmount;
    @FXML private Text paybackCount;
    @FXML private Text paybackAmount;
    @FXML private Text totalPaybackCount;
    @FXML private Text totalPaybackAmount;



    @FXML private Text totalSaleCount;
    @FXML private Text totalSaleAmount;


    @FXML private Text stationName;
    @FXML private Text shiftId;
    @FXML private Text shiftStart;
    @FXML private Text shiftEnd;
    @FXML private Text equipmentId;
    @FXML private Text operatorId;

    @FXML private Text impressMoney;
    @FXML private Text ncmcStock;

    @FXML private Text qrReplacementCount;
    @FXML private Text qrReplacementAmount;
    @FXML private Text ncmcReplacementCount;
    @FXML private Text ncmcReplacementAmount;
    @FXML private Text totalReplacementCount;
    @FXML private Text totalReplacementAmount;

    @FXML private Text sjtRefundCount;
    @FXML private Text sjtRefundAmount;
    @FXML private Text rjtRefundCount;
    @FXML private Text rjtRefundAmount;
    @FXML private Text ncmcRefundCount;
    @FXML private Text ncmcRefundAmount;
    @FXML private Text totalRefundCount;
    @FXML private Text totalRefundAmount;


    @FXML private Text ncmcTopUpCount;
    @FXML private Text ncmcTopUpAmount;
    @FXML private Text totalNcmcTopUpCount;
    @FXML private Text totalNcmcTopUpAmount;

    @FXML private Text totalCash;
    @FXML private Text summaryNcmcTopUp;
    @FXML private Text summaryOthers;
    @FXML private Text totalCardTransactions;
    @FXML private Text totalUpiTransactions;
    @FXML private Text finalTotal;

    @FXML void initialize() {
        // Initialize the UI components if needed
    }

    public void setEOSReport(String shiftId,
                             int finalNoOfSJT,
                             int finalAmountSJT,
                             int finalNoOfRJT,
                             int finalAmountRJT,
                             int finalNoOfGroup,
                             int finalAmountGroup,
                             int finalNoOfFree,
                             int finalAmountFree,
                             int finalNoOfPaid,
                             int finalAmountPaid,
                             int finalNoOfCanceled,
                             int finalAmountCanceled,
                             int finalNoOfAdjusted,
                             int finalAmountAdjusted,
                             int finalNoOfReplaced,
                             int finalAmountReplaced,
                             int finalNoOfRefund,
                             int finalAmountRefunded,
                             LocalDateTime finalLastTransaction,
                             int imprestMoney,
                             Agent agent,
                             int finalTotalAmount,
                             int finalSubTotalCount,
                             String StationName,
                             String shiftStartTime,
                             String shiftEndTime,
                             String mOperatorId
                             ) {

        this.stationName.setText(String.valueOf(StationName));
        this.shiftId.setText(shiftId);
        equipmentId.setText(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId());
        shiftStart.setText(shiftStartTime);
        shiftEnd.setText(shiftEndTime);
        operatorId.setText(mOperatorId);
        impressMoney.setText(String.valueOf(imprestMoney));

        sjtCashAmount.setText(String.valueOf(finalAmountSJT));
        sjtCashCount.setText(String.valueOf(finalNoOfSJT));
        rjtCashCount.setText(String.valueOf(finalNoOfRJT));
        rjtCashAmount.setText(String.valueOf(finalAmountRJT));
        groupCashCount.setText(String.valueOf(finalNoOfGroup));
        groupCashAmount.setText(String.valueOf(finalAmountGroup));

        totalSaleCount.setText(String.valueOf(finalSubTotalCount));
        totalSaleAmount.setText(String.valueOf(finalTotalAmount));

        freeExitCount.setText(String.valueOf(finalNoOfFree));
        freeExitAmount.setText(String.valueOf(finalAmountFree));
        paidExitCount.setText(String.valueOf(finalNoOfPaid));
        paidExitAmount.setText(String.valueOf(finalAmountPaid));
        duplicateTicketCount.setText(String.valueOf(finalNoOfCanceled));
        duplicateTicketAmount.setText(String.valueOf(finalAmountCanceled));

        adjustmentCount.setText(String.valueOf(finalNoOfAdjusted));
        adjustmentAmount.setText(String.valueOf(finalAmountAdjusted));
        totalAdjustmentCount.setText(String.valueOf(finalNoOfAdjusted));
        totalAdjustmentAmount.setText(String.valueOf(finalAmountAdjusted));

        finalTotal.setText(String.valueOf(finalTotalAmount)); // Assuming total cash is the final total amount
        totalSaleAmount.setText(String.valueOf(finalTotalAmount)); // Assuming total sale amount is the final total amount
        totalCash.setText(String.valueOf(finalTotalAmount)); // Assuming total cash is the final total amount

        totalRefundAmount.setText(String.valueOf(finalAmountRefunded));
        totalRefundCount.setText(String.valueOf(finalNoOfRefund));

        duplicateTicketCount.setText(String.valueOf(finalNoOfReplaced));
        duplicateTicketAmount.setText(String.valueOf(finalAmountReplaced));
        totalAdjustmentAmount.setText(String.valueOf(finalAmountReplaced));
        totalAdjustmentCount.setText(String.valueOf(finalNoOfReplaced));




        // Assuming the imprest money value is the total fare medium
    }

    public void setMetadata(String station, String shiftIdValue, String start, String end, String equipment, String operator) {
        stationName.setText(station);
        shiftId.setText(shiftIdValue);
        shiftStart.setText(start);
        shiftEnd.setText(end);
        equipmentId.setText(equipment);
        operatorId.setText(operator);
    }

    public void setStockInfo(String impress, String ncmc) {
        impressMoney.setText(impress);
        ncmcStock.setText(ncmc);
    }
    public void setSaleTransaction(String sjtCount, String sjtAmount, String rjtCount, String rjtAmount, String groupCount, String groupAmount, String cashSubTotalCount, String cashSubTotalAmount) {
        this.sjtCashCount.setText(sjtCount);
        this.sjtCashAmount.setText(sjtAmount);
        this.rjtCashCount.setText(rjtCount);
        this.rjtCashAmount.setText(rjtAmount);
        this.groupCashCount.setText(groupCount);
        this.groupCashAmount.setText(groupAmount);
        this.totalSaleCount.setText(cashSubTotalCount);
        this.totalSaleAmount.setText(cashSubTotalAmount);
    }


//    F
    public void setReplacementInfo(String qrCount, String qrAmount, String ncmcCount, String ncmcAmount, String totalCount, String totalAmount) {
        qrReplacementCount.setText(qrCount);
        qrReplacementAmount.setText(qrAmount);
        ncmcReplacementCount.setText(ncmcCount);
        ncmcReplacementAmount.setText(ncmcAmount);
        totalReplacementCount.setText(totalCount);
        totalReplacementAmount.setText(totalAmount);
    }

//    G
    public void setRefundInfo(String sjtCount, String sjtAmount, String rjtCount, String rjtAmount, String ncmcCount, String ncmcAmount, String totalCount, String totalAmount) {
        sjtRefundCount.setText(sjtCount);
        sjtRefundAmount.setText(sjtAmount);
        rjtRefundCount.setText(rjtCount);
        rjtRefundAmount.setText(rjtAmount);
        ncmcRefundCount.setText(ncmcCount);
        ncmcRefundAmount.setText(ncmcAmount);
        totalRefundCount.setText(totalCount);
        totalRefundAmount.setText(totalAmount);
    }


    public void setNcmcTopUpInfo(String topUpCount, String topUpAmount, String totalCount, String totalAmount) {
        ncmcTopUpCount.setText(topUpCount);
        ncmcTopUpAmount.setText(topUpAmount);
        totalNcmcTopUpCount.setText(totalCount);
        totalNcmcTopUpAmount.setText(totalAmount);
    }

    public void setSummary(String cash, String ncmcTopUp, String others, String cardTx, String upiTx, String finalAmt) {
        totalCash.setText(cash);
        summaryNcmcTopUp.setText(ncmcTopUp);
        summaryOthers.setText(others);
        totalCardTransactions.setText(cardTx);
        totalUpiTransactions.setText(upiTx);
        finalTotal.setText(finalAmt);
    }




}
