package com.amay.tom.model.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class  UserPrivilege  implements AutoCloseable{
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


    @Override
    public void close() throws Exception {

    }
}
