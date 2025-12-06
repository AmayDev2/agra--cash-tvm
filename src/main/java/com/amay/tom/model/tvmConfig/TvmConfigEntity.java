package com.amay.tom.model.tvmConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class TvmConfigEntity {
    private int id;
    private String configVer;
    private int maxChangeDispense;
    private int maxCoinDispensedQuantity;
    private int maxCoinDispensedTotalQuantity;
    private int maxBnrAcceptAmount;
    private int maxBnrDispensedAmount;
    private int maxBnrDispensedQuantity;
    private int maxBnrDispensedTotalQuantity;
    private int hopper1UnitAmount;
    private int hopper2UnitAmount;
    private int hopper3UnitAmount;
    private int transactionTimeout;
    private int paymentScreenTimeout;
    private int idleScreenTimeout;
    private boolean bnrEnabled;
    private boolean coinDispenserEnabled;
    private boolean posEnabled;
    private boolean upiEnabled;
    private boolean receiptPrinterEnabled;
}
