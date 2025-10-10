package com.amay.tom.model.ccuRest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TVMPermission implements Serializable {

    private long id;

    private boolean bnrCashAdd;
    private boolean bnrTesting;
    private boolean bnrMoveCash;
    private boolean checkAvailableCash;
    private boolean coinRefill;
    private boolean coinModuleTesting;
    private boolean coinDumping;
    private boolean peripheralTest;
    private boolean configuration;
    private boolean modeSettings;
    private boolean versionCheck;
    private boolean importAndExport;
    private boolean shutdownAndRestart;

    
    // Getters and setters...
    
    
}
