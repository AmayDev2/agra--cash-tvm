package com.amay.tom.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterConfigInfoCheck {
        private int id;
        private boolean configVer;
        private boolean tomConfig;
        private boolean agConfig;
        private boolean tvmConfig;
        private boolean trConfig;
        private boolean ticketConfig;
        private boolean businessDayVer;
        private boolean peakTimeVer;
        private boolean calenderConfig;
        private boolean fareConfig;
        private boolean topologyConfig;
        private boolean userVer;
        private boolean profileVer;
        private boolean tomSwVer;
        private boolean agSwVer;
        private boolean tvmSwVer;
        private boolean trSwVer;
        private boolean scSwVer;
        private boolean productConfig;
}
