package com.amay.tom.model.equipment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {
    private String lineId;
    private String stationId;
    private String equipmentTypeId;
    private String equipmentSerial;
    private String equipmentId;
    private String equipmentName;
    private String equipmentIp;
    private String status;


    public Equipment(String stationId, String equipmentTypeId, String equipmentSerial, String equipmentId, String equipmentName, String equipmentIp, String status) {

        this.stationId = stationId;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentSerial = equipmentSerial;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentIp = equipmentIp;
        this.status = status;
    }

    private boolean beta;

    public Equipment(String lineId, String stationId, String equipmentTypeId, String equipmentSerial, String equipmentId, String equipmentName, String equipmentIp, String status) {
        this.lineId = lineId;
        this.stationId = stationId;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentSerial = equipmentSerial;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentIp = equipmentIp;
        this.status = status;
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;

        Equipment that = (Equipment) o;
        return Objects.equals(lineId, that.lineId) &&
                Objects.equals(stationId, that.stationId) &&
                Objects.equals(equipmentTypeId, that.equipmentTypeId) &&
                Objects.equals(equipmentSerial, that.equipmentSerial) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(equipmentName, that.equipmentName) &&
                Objects.equals(equipmentIp, that.equipmentIp) &&
                Objects.equals(status, that.status);
    }
}
