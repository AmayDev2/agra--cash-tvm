package com.amay.tom.model.business;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeakTimeConfig {
    private int id;
    private String configVer;
    private String peakTimeName;
    private String startTime;
    private String endTime;
    private double fareMultiplier;
    private String status;
}