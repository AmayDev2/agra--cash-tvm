package com.amay.tom.model.version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterConfigInfoEntity {
    private String configVer;
    private String tomConfig;
    private String agConfig;
    private String tvmConfig;
    private String trConfig;
    private String ticketConfig;
    private String businessDayVer;
    private String peakTimeVer;
    private String calenderConfig;
    private String fareConfig;
    private String topologyConfig;
    private String userVer;
    private String profileVer;
    private String tomSwVer;
    private String agSwVer;
    private String tvmSwVer;
    private String trSwVer;
    private String scSwVer;
    private String productConfig;
}
