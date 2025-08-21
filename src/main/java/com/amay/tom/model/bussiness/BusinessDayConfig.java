package com.amay.tom.model.bussiness;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessDayConfig {
    private int id;
    private String configVer;
    private String businessDayName;
    private String dayType;
    private String startTime;
    private String endTime;
    private double fareMultiplier;
    private String status;
}