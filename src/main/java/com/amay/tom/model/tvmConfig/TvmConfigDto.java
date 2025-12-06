package com.amay.tom.model.tvmConfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvmConfigDto {
    private int id;
    private String configVer;

    private int maxChangeDispense;
    private int maxCoinDispensedQuantity=100;
    private int maxCoinDispensedTotalQuantity=100;
    private int maxBnrAcceptAmount=100;
    private int maxBnrDispensedAmount=100;
    private int maxBnrDispensedQuantity=100;
    private int maxBnrDispensedTotalQuantity=100;
    private int hopper1UnitAmount=5;
    private int hopper2UnitAmount=10;
    private int hopper3UnitAmount=10;
    private int transactionTimeout;
    private int paymentScreenTimeout;
    private int idleScreenTimeout;
    private boolean bnrEnabled;
    private boolean coinDispenserEnabled;
    private boolean posEnabled;
    private boolean upiEnabled;
    private boolean receiptPrinterEnabled;
}
