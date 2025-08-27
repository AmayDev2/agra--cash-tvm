package com.amay.tom.model.business;

import com.amay.tom.config.dto.BusinessDayConfigDTO;
import com.amay.tom.model.bussiness.BusinessDayConfig;

public class BusinessDayConfigMapper {

    public static BusinessDayConfigEntity dtoToEntity(BusinessDayConfigDTO dto) {
        if (dto == null) return null;
        return new BusinessDayConfigEntity()
                .setId(dto.getId())
                .setConfigVer(dto.getConfigVer())
                .setBusinessDayName(dto.getBusinessDayName())
                .setDayType(dto.getDayType())
                .setStartTime(dto.getStartTime())
                .setEndTime(dto.getEndTime())
                .setFareMultiplier(dto.getFareMultiplier())
                .setStatus(dto.getStatus());
    }

    public static BusinessDayConfigDTO entityToDto(BusinessDayConfigEntity entity) {
        if (entity == null) return null;
        return new BusinessDayConfigDTO()
                .setId(entity.getId())
                .setConfigVer(entity.getConfigVer())
                .setBusinessDayName(entity.getBusinessDayName())
                .setDayType(entity.getDayType())
                .setStartTime(entity.getStartTime())
                .setEndTime(entity.getEndTime())
                .setFareMultiplier(entity.getFareMultiplier())
                .setStatus(entity.getStatus());
    }

    public static BusinessDayConfig dtoToModel(BusinessDayConfigDTO dto) {
        if (dto == null) return null;
        return new BusinessDayConfig()
                .setId(dto.getId())
                .setConfigVer(dto.getConfigVer())
                .setBusinessDayName(dto.getBusinessDayName())
                .setDayType(dto.getDayType())
                .setStartTime(dto.getStartTime())
                .setEndTime(dto.getEndTime())
                .setFareMultiplier(dto.getFareMultiplier())
                .setStatus(dto.getStatus());
    }

    public static BusinessDayConfigDTO modelToDto(BusinessDayConfig model) {
        if (model == null) return null;
        return new BusinessDayConfigDTO()
                .setId(model.getId())
                .setConfigVer(model.getConfigVer())
                .setBusinessDayName(model.getBusinessDayName())
                .setDayType(model.getDayType())
                .setStartTime(model.getStartTime())
                .setEndTime(model.getEndTime())
                .setFareMultiplier(model.getFareMultiplier())
                .setStatus(model.getStatus());
    }
}

