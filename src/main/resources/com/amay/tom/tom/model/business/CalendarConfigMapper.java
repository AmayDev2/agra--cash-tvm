package com.amay.tom.model.business;

import com.amay.tom.config.dto.CalendarConfigDTO;

public class CalendarConfigMapper {

    public static CalendarConfigEntity dtoToEntity(CalendarConfigDTO dto) {
        if (dto == null) return null;
        return new CalendarConfigEntity()
                .setConfigVer(dto.getConfigVer())
                .setSpecialDayName(dto.getSpecialDayName())
                .setStatus(dto.getStatus())
                .setSpecialDate(dto.getSpecialDate());
    }

    public static CalendarConfigDTO entityToDto(CalendarConfigEntity entity) {
        if (entity == null) return null;
        return new CalendarConfigDTO()
                .setConfigVer(entity.getConfigVer())
                .setSpecialDayName(entity.getSpecialDayName())
                .setStatus(entity.getStatus())
                .setSpecialDate(entity.getSpecialDate());
    }

    public static CalendarConfig dtoToModel(CalendarConfigDTO dto) {
        if (dto == null) return null;
        return new CalendarConfig()
                .setConfigVer(dto.getConfigVer())
                .setSpecialDayName(dto.getSpecialDayName())
                .setStatus(dto.getStatus())
                .setSpecialDate(dto.getSpecialDate());
    }

    public static CalendarConfigDTO modelToDto(CalendarConfig model) {
        if (model == null) return null;
        return new CalendarConfigDTO()
                .setConfigVer(model.getConfigVer())
                .setSpecialDayName(model.getSpecialDayName())
                .setStatus(model.getStatus())
                .setSpecialDate(model.getSpecialDate());
    }
}
