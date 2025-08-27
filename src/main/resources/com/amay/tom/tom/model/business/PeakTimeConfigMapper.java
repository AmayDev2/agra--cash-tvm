package com.amay.tom.model.business;

import com.amay.tom.config.dto.PeakTimeConfigDTO;
import com.amay.tom.model.bussiness.PeakTimeConfig;

public class PeakTimeConfigMapper {

    public static PeakTimeConfigEntity dtoToEntity(PeakTimeConfigDTO dto) {
        if (dto == null) return null;
        return new PeakTimeConfigEntity()
                .setId(dto.getId())
                .setConfigVer(dto.getConfigVer())
                .setPeakTimeName(dto.getPeakTimeName())
                .setStartTime(dto.getStartTime())
                .setEndTime(dto.getEndTime())
                .setFareMultiplier(dto.getFareMultiplier())
                .setStatus(dto.getStatus());
    }

    public static PeakTimeConfigDTO entityToDto(PeakTimeConfigEntity entity) {
        if (entity == null) return null;
        return new PeakTimeConfigDTO()
                .setId(entity.getId())
                .setConfigVer(entity.getConfigVer())
                .setPeakTimeName(entity.getPeakTimeName())
                .setStartTime(entity.getStartTime())
                .setEndTime(entity.getEndTime())
                .setFareMultiplier(entity.getFareMultiplier())
                .setStatus(entity.getStatus());
    }

    public static PeakTimeConfig dtoToModel(PeakTimeConfigDTO dto) {
        if (dto == null) return null;
        return new PeakTimeConfig()
                .setId(dto.getId())
                .setConfigVer(dto.getConfigVer())
                .setPeakTimeName(dto.getPeakTimeName())
                .setStartTime(dto.getStartTime())
                .setEndTime(dto.getEndTime())
                .setFareMultiplier(dto.getFareMultiplier())
                .setStatus(dto.getStatus());
    }

    public static PeakTimeConfigDTO modelToDto(PeakTimeConfig model) {
        if (model == null) return null;
        return new PeakTimeConfigDTO()
                .setId(model.getId())
                .setConfigVer(model.getConfigVer())
                .setPeakTimeName(model.getPeakTimeName())
                .setStartTime(model.getStartTime())
                .setEndTime(model.getEndTime())
                .setFareMultiplier(model.getFareMultiplier())
                .setStatus(model.getStatus());
    }
}

