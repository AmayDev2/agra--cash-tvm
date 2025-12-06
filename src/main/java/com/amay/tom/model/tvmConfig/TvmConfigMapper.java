package com.amay.tom.model.tvmConfig;

public class TvmConfigMapper {
    public static TvmConfigDto modelToDto(TvmConfig model) {
        if (model == null) return null;
        TvmConfigDto dto = new TvmConfigDto();
        dto.setId(model.getId());
        dto.setConfigVer(model.getConfigVer());
        dto.setMaxChangeDispense(model.getMaxChangeDispense());
        dto.setMaxCoinDispensedQuantity(model.getMaxCoinDispensedQuantity());
        dto.setMaxCoinDispensedTotalQuantity(model.getMaxCoinDispensedTotalQuantity());
        dto.setMaxBnrAcceptAmount(model.getMaxBnrAcceptAmount());
        dto.setMaxBnrDispensedAmount(model.getMaxBnrDispensedAmount());
        dto.setMaxBnrDispensedQuantity(model.getMaxBnrDispensedQuantity());
        dto.setMaxBnrDispensedTotalQuantity(model.getMaxBnrDispensedTotalQuantity());
        dto.setHopper1UnitAmount(model.getHopper1UnitAmount());
        dto.setHopper2UnitAmount(model.getHopper2UnitAmount());
        dto.setHopper3UnitAmount(model.getHopper3UnitAmount());
        dto.setTransactionTimeout(model.getTransactionTimeout());
        dto.setPaymentScreenTimeout(model.getPaymentScreenTimeout());
        dto.setIdleScreenTimeout(model.getIdleScreenTimeout());
        dto.setBnrEnabled(model.isBnrEnabled());
        dto.setCoinDispenserEnabled(model.isCoinDispenserEnabled());
        dto.setPosEnabled(model.isPosEnabled());
        dto.setUpiEnabled(model.isUpiEnabled());
        dto.setReceiptPrinterEnabled(model.isReceiptPrinterEnabled());
        return dto;
    }

    public static TvmConfig dtoToModel(TvmConfigDto dto) {
        if (dto == null) return null;
        TvmConfig model = new TvmConfig();
        model.setId(dto.getId());
        model.setConfigVer(dto.getConfigVer());
        model.setMaxChangeDispense(dto.getMaxChangeDispense());
        model.setMaxCoinDispensedQuantity(dto.getMaxCoinDispensedQuantity());
        model.setMaxCoinDispensedTotalQuantity(dto.getMaxCoinDispensedTotalQuantity());
        model.setMaxBnrAcceptAmount(dto.getMaxBnrAcceptAmount());
        model.setMaxBnrDispensedAmount(dto.getMaxBnrDispensedAmount());
        model.setMaxBnrDispensedQuantity(dto.getMaxBnrDispensedQuantity());
        model.setMaxBnrDispensedTotalQuantity(dto.getMaxBnrDispensedTotalQuantity());
        model.setHopper1UnitAmount(dto.getHopper1UnitAmount());
        model.setHopper2UnitAmount(dto.getHopper2UnitAmount());
        model.setHopper3UnitAmount(dto.getHopper3UnitAmount());
        model.setTransactionTimeout(dto.getTransactionTimeout());
        model.setPaymentScreenTimeout(dto.getPaymentScreenTimeout());
        model.setIdleScreenTimeout(dto.getIdleScreenTimeout());
        model.setBnrEnabled(dto.isBnrEnabled());
        model.setCoinDispenserEnabled(dto.isCoinDispenserEnabled());
        model.setPosEnabled(dto.isPosEnabled());
        model.setUpiEnabled(dto.isUpiEnabled());
        model.setReceiptPrinterEnabled(dto.isReceiptPrinterEnabled());
        return model;
    }

    // ===============================
    // Model <-> Entity
    // ===============================
    public static TvmConfigEntity modelToEntity(TvmConfig model) {
        if (model == null) return null;
        TvmConfigEntity entity = new TvmConfigEntity();
        entity.setId(model.getId());
        entity.setConfigVer(model.getConfigVer());
        entity.setMaxChangeDispense(model.getMaxChangeDispense());
        entity.setMaxCoinDispensedQuantity(model.getMaxCoinDispensedQuantity());
        entity.setMaxCoinDispensedTotalQuantity(model.getMaxCoinDispensedTotalQuantity());
        entity.setMaxBnrAcceptAmount(model.getMaxBnrAcceptAmount());
        entity.setMaxBnrDispensedAmount(model.getMaxBnrDispensedAmount());
        entity.setMaxBnrDispensedQuantity(model.getMaxBnrDispensedQuantity());
        entity.setMaxBnrDispensedTotalQuantity(model.getMaxBnrDispensedTotalQuantity());
        entity.setHopper1UnitAmount(model.getHopper1UnitAmount());
        entity.setHopper2UnitAmount(model.getHopper2UnitAmount());
        entity.setHopper3UnitAmount(model.getHopper3UnitAmount());
        entity.setTransactionTimeout(model.getTransactionTimeout());
        entity.setPaymentScreenTimeout(model.getPaymentScreenTimeout());
        entity.setIdleScreenTimeout(model.getIdleScreenTimeout());
        entity.setBnrEnabled(model.isBnrEnabled());
        entity.setCoinDispenserEnabled(model.isCoinDispenserEnabled());
        entity.setPosEnabled(model.isPosEnabled());
        entity.setUpiEnabled(model.isUpiEnabled());
        entity.setReceiptPrinterEnabled(model.isReceiptPrinterEnabled());
        return entity;
    }

    public static TvmConfig entityToModel(TvmConfigEntity entity) {
        if (entity == null) return null;
        TvmConfig model = new TvmConfig();
        model.setId(entity.getId());
        model.setConfigVer(entity.getConfigVer());
        model.setMaxChangeDispense(entity.getMaxChangeDispense());
        model.setMaxCoinDispensedQuantity(entity.getMaxCoinDispensedQuantity());
        model.setMaxCoinDispensedTotalQuantity(entity.getMaxCoinDispensedTotalQuantity());
        model.setMaxBnrAcceptAmount(entity.getMaxBnrAcceptAmount());
        model.setMaxBnrDispensedAmount(entity.getMaxBnrDispensedAmount());
        model.setMaxBnrDispensedQuantity(entity.getMaxBnrDispensedQuantity());
        model.setMaxBnrDispensedTotalQuantity(entity.getMaxBnrDispensedTotalQuantity());
        model.setHopper1UnitAmount(entity.getHopper1UnitAmount());
        model.setHopper2UnitAmount(entity.getHopper2UnitAmount());
        model.setHopper3UnitAmount(entity.getHopper3UnitAmount());
        model.setTransactionTimeout(entity.getTransactionTimeout());
        model.setPaymentScreenTimeout(entity.getPaymentScreenTimeout());
        model.setIdleScreenTimeout(entity.getIdleScreenTimeout());
        model.setBnrEnabled(entity.isBnrEnabled());
        model.setCoinDispenserEnabled(entity.isCoinDispenserEnabled());
        model.setPosEnabled(entity.isPosEnabled());
        model.setUpiEnabled(entity.isUpiEnabled());
        model.setReceiptPrinterEnabled(entity.isReceiptPrinterEnabled());
        return model;
    }

    // ===============================
    // DTO <-> Entity
    // ===============================
    public static TvmConfigEntity dtoToEntity(TvmConfigDto dto) {
        if (dto == null) return null;
        TvmConfigEntity entity = new TvmConfigEntity();
        entity.setId(dto.getId());
        entity.setConfigVer(dto.getConfigVer());
        entity.setMaxChangeDispense(dto.getMaxChangeDispense());
        entity.setMaxCoinDispensedQuantity(dto.getMaxCoinDispensedQuantity());
        entity.setMaxCoinDispensedTotalQuantity(dto.getMaxCoinDispensedTotalQuantity());
        entity.setMaxBnrAcceptAmount(dto.getMaxBnrAcceptAmount());
        entity.setMaxBnrDispensedAmount(dto.getMaxBnrDispensedAmount());
        entity.setMaxBnrDispensedQuantity(dto.getMaxBnrDispensedQuantity());
        entity.setMaxBnrDispensedTotalQuantity(dto.getMaxBnrDispensedTotalQuantity());
        entity.setHopper1UnitAmount(dto.getHopper1UnitAmount());
        entity.setHopper2UnitAmount(dto.getHopper2UnitAmount());
        entity.setHopper3UnitAmount(dto.getHopper3UnitAmount());
        entity.setTransactionTimeout(dto.getTransactionTimeout());
        entity.setPaymentScreenTimeout(dto.getPaymentScreenTimeout());
        entity.setIdleScreenTimeout(dto.getIdleScreenTimeout());
        entity.setBnrEnabled(dto.isBnrEnabled());
        entity.setCoinDispenserEnabled(dto.isCoinDispenserEnabled());
        entity.setPosEnabled(dto.isPosEnabled());
        entity.setUpiEnabled(dto.isUpiEnabled());
        entity.setReceiptPrinterEnabled(dto.isReceiptPrinterEnabled());
        return entity;
    }

    public static TvmConfigDto entityToDto(TvmConfigEntity entity) {
        if (entity == null) return null;
        TvmConfigDto dto = new TvmConfigDto();
        dto.setId(entity.getId());
        dto.setConfigVer(entity.getConfigVer());
        dto.setMaxChangeDispense(entity.getMaxChangeDispense());
        dto.setMaxCoinDispensedQuantity(entity.getMaxCoinDispensedQuantity());
        dto.setMaxCoinDispensedTotalQuantity(entity.getMaxCoinDispensedTotalQuantity());
        dto.setMaxBnrAcceptAmount(entity.getMaxBnrAcceptAmount());
        dto.setMaxBnrDispensedAmount(entity.getMaxBnrDispensedAmount());
        dto.setMaxBnrDispensedQuantity(entity.getMaxBnrDispensedQuantity());
        dto.setMaxBnrDispensedTotalQuantity(entity.getMaxBnrDispensedTotalQuantity());
        dto.setHopper1UnitAmount(entity.getHopper1UnitAmount());
        dto.setHopper2UnitAmount(entity.getHopper2UnitAmount());
        dto.setHopper3UnitAmount(entity.getHopper3UnitAmount());
        dto.setTransactionTimeout(entity.getTransactionTimeout());
        dto.setPaymentScreenTimeout(entity.getPaymentScreenTimeout());
        dto.setIdleScreenTimeout(entity.getIdleScreenTimeout());
        dto.setBnrEnabled(entity.isBnrEnabled());
        dto.setCoinDispenserEnabled(entity.isCoinDispenserEnabled());
        dto.setPosEnabled(entity.isPosEnabled());
        dto.setUpiEnabled(entity.isUpiEnabled());
        dto.setReceiptPrinterEnabled(entity.isReceiptPrinterEnabled());
        return dto;
    }
}
