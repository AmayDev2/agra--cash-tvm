package com.amay.tom.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarConfigDTO {
    private String configVer;
    private String specialDayName;
    private String status;
    private String specialDate;
}
