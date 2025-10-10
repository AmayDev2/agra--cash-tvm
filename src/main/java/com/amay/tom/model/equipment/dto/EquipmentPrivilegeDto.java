package com.amay.tom.model.equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentPrivilegeDto {
    private boolean bnrCashAdd = true;
    private boolean bnrTesting = true;
    private boolean bnrMoveCash = true;
    private boolean checkAvailableCash = true;
    private boolean coinRefill = true;
    private boolean coinModuleTesting = true;
    private boolean coinDumping = true;
    private boolean peripheralTest = true;
    private boolean configuration = true;
    private boolean modeSettings = true;
    private boolean versionCheck = true;
    private boolean importAndExport = true;
    private boolean shutdownAndRestart = true;

}
