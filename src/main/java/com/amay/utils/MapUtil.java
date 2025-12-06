package com.amay.utils;

import com.amay.tom.model.tvmConfig.TvmConfig;

public class MapUtil {
    public static int hopperIdToUnitAmount(int id, TvmConfig tvmConfig){
        return switch (id) {
            case 1 -> tvmConfig.getHopper1UnitAmount();
            case 2 -> tvmConfig.getHopper2UnitAmount();
            case 3 -> tvmConfig.getHopper3UnitAmount();
            default -> -1;
        };
    }
}
