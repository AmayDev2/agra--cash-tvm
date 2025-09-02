package com.amay.tom.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationData1 {
    private int id;
    private String lineId;
    private String stationId;
    private String stationCode;
    private String stationName;
    private String status;
    private String stationIp;
    private String stationPort;
    private String createdBy;
    private String updatedBy;
}