package com.amay.tom.model.equipment;

import com.amay.tom.model.equipment.dto.EquipmentPrivilegeDto;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
import javafx.beans.property.SimpleBooleanProperty;

public class EquipmentMapper {

    public static EquipmentPrivilege mapToEquipmentPrivilege(EquipmentPrivilegeDto equipmentPrivilegeDto) {
        return new EquipmentPrivilege(
                new SimpleBooleanProperty(equipmentPrivilegeDto.isBnrCashAdd()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isBnrTesting()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isBnrMoveCash()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isCheckAvailableCash()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isCoinRefill()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isCoinModuleTesting()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isCoinDumping()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isPeripheralTest()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isConfiguration()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isModeSettings()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isVersionCheck()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isImportAndExport()),
                new SimpleBooleanProperty(equipmentPrivilegeDto.isShutdownAndRestart())
        );
    }

    public static EquipmentPrivilege mapToEquipmentPrivilege(EquipmentPrivilegeDto equipmentPrivilegeDto, EquipmentPrivilege equipmentPrivilege) {
        equipmentPrivilege.setBnrCashAdd(new SimpleBooleanProperty(equipmentPrivilegeDto.isBnrCashAdd()));
        equipmentPrivilege.setBnrTesting(new SimpleBooleanProperty(equipmentPrivilegeDto.isBnrTesting()));
        equipmentPrivilege.setBnrMoveCash(new SimpleBooleanProperty(equipmentPrivilegeDto.isBnrMoveCash()));
        equipmentPrivilege.setCheckAvailableCash(new SimpleBooleanProperty(equipmentPrivilegeDto.isCheckAvailableCash()));
        equipmentPrivilege.setCoinRefill(new SimpleBooleanProperty(equipmentPrivilegeDto.isCoinRefill()));
        equipmentPrivilege.setCoinModuleTesting(new SimpleBooleanProperty(equipmentPrivilegeDto.isCoinModuleTesting()));
        equipmentPrivilege.setCoinDumping(new SimpleBooleanProperty(equipmentPrivilegeDto.isCoinDumping()));
        equipmentPrivilege.setPeripheralTest(new SimpleBooleanProperty(equipmentPrivilegeDto.isPeripheralTest()));
        equipmentPrivilege.setConfiguration(new SimpleBooleanProperty(equipmentPrivilegeDto.isConfiguration()));
        equipmentPrivilege.setModeSettings(new SimpleBooleanProperty(equipmentPrivilegeDto.isModeSettings()));
        equipmentPrivilege.setVersionCheck(new SimpleBooleanProperty(equipmentPrivilegeDto.isVersionCheck()));
        equipmentPrivilege.setImportAndExport(new SimpleBooleanProperty(equipmentPrivilegeDto.isImportAndExport()));
        equipmentPrivilege.setShutdownAndRestart(new SimpleBooleanProperty(equipmentPrivilegeDto.isShutdownAndRestart()));
        return equipmentPrivilege;
    }

    public static EquipmentPrivilege testMapToEquipmentPrivilege() {
        return new EquipmentPrivilege(
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true),
                new SimpleBooleanProperty(true)
        );
    }



}
