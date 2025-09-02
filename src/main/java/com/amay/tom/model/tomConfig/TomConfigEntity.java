package com.amay.tom.model.tomConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class TomConfigEntity{
    private int id;
    private String configVer;
    private int shiftPauseDuration;
    private int cartLimit;
    private int maxDaysOffline;
    private int offlineRefund;
}
