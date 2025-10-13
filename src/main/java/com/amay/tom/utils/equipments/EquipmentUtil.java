package com.amay.tom.utils.equipments;

import com.amay.tom.model.equipment.entity.Equipment;
import lombok.Getter;
import lombok.Setter;

public class EquipmentUtil {
    @Getter
    @Setter
    private static Equipment equipment;

    private EquipmentUtil() {} // prevent instantiation
}