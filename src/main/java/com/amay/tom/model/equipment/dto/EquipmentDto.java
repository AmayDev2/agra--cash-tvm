package com.amay.tom.model.equipment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EquipmentDto {
    @JsonProperty("lineId")
    private String lineId;

    @JsonProperty("stationId")
    private String stationId;

    @JsonProperty("equipmentTypeId")
    private String equipmentTypeId;

    @JsonProperty("equipmentSerial")
    private String equipmentSerial;

    @JsonProperty("equipmentId")
    private String equipmentId;

    @JsonProperty("equipmentName")
    private String equipmentName;

    @JsonProperty("equipmentIp")
    private String equipmentIp;

    @JsonProperty("status")
    private String status;

    @JsonProperty("beta")
    private boolean beta;
}
