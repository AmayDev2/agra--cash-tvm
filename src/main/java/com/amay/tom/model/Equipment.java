package com.amay.tom.model;

public class Equipment {
    private final String equipmentId;
    private final String equipmentSerial;
    private final String equipmentName;



    public String getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentSerial() {
        return equipmentSerial;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public Equipment(String equipmentId, String equipmentSerial) {
        this.equipmentId = equipmentId;
        this.equipmentSerial = equipmentSerial;
        this.equipmentName= (equipmentId.startsWith("01", 4)?"TOM": equipmentId.startsWith("02", 4)?"EFO":equipmentId.startsWith("03", 4)?"TVM":"UNKNOWN") + " - " + equipmentSerial;
    }
}
