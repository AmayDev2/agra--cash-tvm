package com.amay.tom.model.equipment;

import com.amay.tom.model.equipment.dto.EquipmentDto;
import com.amay.tom.model.equipment.dto.EquipmentPrivilegeDto;
import com.amay.tom.model.equipment.entity.Equipment;
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

    public static Equipment convertEquipmentDtoToEntity(EquipmentDto dto) {
        if (dto == null) return null;

        Equipment equipment = new Equipment();
        equipment.setLineId(dto.getLineId());
        equipment.setStationId(dto.getStationId());
        equipment.setEquipmentTypeId(dto.getEquipmentTypeId());
        equipment.setEquipmentSerial(dto.getEquipmentSerial());
        equipment.setEquipmentId(dto.getEquipmentId());
        equipment.setEquipmentName(dto.getEquipmentName());
        equipment.setEquipmentIp(dto.getEquipmentIp());
        equipment.setStatus(dto.getStatus());
        equipment.setBeta(dto.isBeta());
        return equipment;
    }

    /** Convert Equipment entity to EquipmentDto */
    public static EquipmentDto convertEquipmentEntityToDto(Equipment entity) {
        if (entity == null) return null;

        EquipmentDto dto = new EquipmentDto();
        dto.setLineId(entity.getLineId());
        dto.setStationId(entity.getStationId());
        dto.setEquipmentTypeId(entity.getEquipmentTypeId());
        dto.setEquipmentSerial(entity.getEquipmentSerial());
        dto.setEquipmentId(entity.getEquipmentId());
        dto.setEquipmentName(entity.getEquipmentName());
        dto.setEquipmentIp(entity.getEquipmentIp());
        dto.setStatus(entity.getStatus());
        dto.setBeta(entity.isBeta());
        return dto;
    }


}
