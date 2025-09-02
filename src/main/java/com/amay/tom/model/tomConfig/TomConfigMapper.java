package com.amay.tom.model.tomConfig;

public class TomConfigMapper {

    // ===============================
    // Model <-> DTO
    // ===============================
    public static TomConfigDto modelToDto(TomConfig model) {
        if (model == null) return null;
        TomConfigDto dto = new TomConfigDto();
        dto.setId(model.getId());
        dto.setConfigVer(model.getConfigVer());
        dto.setShiftPauseDuration(model.getShiftPauseDuration());
        dto.setCartLimit(model.getCartLimit());
        dto.setMaxDaysOffline(model.getMaxDaysOffline());
        dto.setOfflineRefund(model.getOfflineRefund());
        return dto;
    }

    public static TomConfig dtoToModel(TomConfigDto dto) {
        if (dto == null) return null;
        TomConfig model = new TomConfig();
        model.setId(dto.getId());
        model.setConfigVer(dto.getConfigVer());
        model.setShiftPauseDuration(dto.getShiftPauseDuration());
        model.setCartLimit(dto.getCartLimit());
        model.setMaxDaysOffline(dto.getMaxDaysOffline());
        model.setOfflineRefund(dto.getOfflineRefund());
        return model;
    }

    // ===============================
    // Model <-> Entity
    // ===============================
    public static TomConfigEntity modelToEntity(TomConfig model) {
        if (model == null) return null;
        TomConfigEntity entity = new TomConfigEntity();
        entity.setId(model.getId());
        entity.setConfigVer(model.getConfigVer());
        entity.setShiftPauseDuration(model.getShiftPauseDuration());
        entity.setCartLimit(model.getCartLimit());
        entity.setMaxDaysOffline(model.getMaxDaysOffline());
        entity.setOfflineRefund(model.getOfflineRefund());
        return entity;
    }

    public static TomConfig entityToModel(TomConfigEntity entity) {
        if (entity == null) return null;
        TomConfig model = new TomConfig();
        model.setId(entity.getId());
        model.setConfigVer(entity.getConfigVer());
        model.setShiftPauseDuration(entity.getShiftPauseDuration());
        model.setCartLimit(entity.getCartLimit());
        model.setMaxDaysOffline(entity.getMaxDaysOffline());
        model.setOfflineRefund(entity.getOfflineRefund());
        return model;
    }

    // ===============================
    // DTO <-> Entity
    // ===============================
    public static TomConfigEntity dtoToEntity(TomConfigDto dto) {
        if (dto == null) return null;
        TomConfigEntity entity = new TomConfigEntity();
        entity.setId(dto.getId());
        entity.setConfigVer(dto.getConfigVer());
        entity.setShiftPauseDuration(dto.getShiftPauseDuration());
        entity.setCartLimit(dto.getCartLimit());
        entity.setMaxDaysOffline(dto.getMaxDaysOffline());
        entity.setOfflineRefund(dto.getOfflineRefund());
        return entity;
    }

    public static TomConfigDto entityToDto(TomConfigEntity entity) {
        if (entity == null) return null;
        TomConfigDto dto = new TomConfigDto();
        dto.setId(entity.getId());
        dto.setConfigVer(entity.getConfigVer());
        dto.setShiftPauseDuration(entity.getShiftPauseDuration());
        dto.setCartLimit(entity.getCartLimit());
        dto.setMaxDaysOffline(entity.getMaxDaysOffline());
        dto.setOfflineRefund(entity.getOfflineRefund());
        return dto;
    }
}

