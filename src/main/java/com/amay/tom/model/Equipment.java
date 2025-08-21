package com.amay.tom.model;

public class Equipment {
    private String equipmentId;
    private String equipmentSerial;



    public String getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentSerial() {
        return equipmentSerial;
    }

    public Equipment(String equipmentId, String equipmentSerial) {
        this.equipmentId = equipmentId;
        this.equipmentSerial = equipmentSerial;
    }
}
