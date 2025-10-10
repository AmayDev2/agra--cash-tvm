package com.amay.tom.model.equipment.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class EquipmentPrivilege {
    private BooleanProperty bnrCashAdd = new SimpleBooleanProperty();
    private BooleanProperty bnrTesting = new SimpleBooleanProperty();
    private BooleanProperty bnrMoveCash = new SimpleBooleanProperty();
    private BooleanProperty checkAvailableCash = new SimpleBooleanProperty();
    private BooleanProperty coinRefill = new SimpleBooleanProperty();
    private BooleanProperty coinModuleTesting = new SimpleBooleanProperty();
    private BooleanProperty coinDumping = new SimpleBooleanProperty();
    private BooleanProperty peripheralTest = new SimpleBooleanProperty();
    private BooleanProperty configuration = new SimpleBooleanProperty();
    private BooleanProperty modeSettings = new SimpleBooleanProperty();
    private BooleanProperty versionCheck = new SimpleBooleanProperty();
    private BooleanProperty importAndExport = new SimpleBooleanProperty();
    private BooleanProperty shutdownAndRestart = new SimpleBooleanProperty();
}
