package com.amay.tom.model.version;

import com.amay.tom.config.dto.MasterConfigInfoDTO;

public class MasterConfigInfoMapper {

    private MasterConfigInfoMapper() {
        // Prevent instantiation
    }

    // DTO → Domain Model
    public static MasterConfigInfo dtoToMasterConfigInfo(MasterConfigInfoDTO dto) {
        if (dto == null) return null;
        MasterConfigInfo info = new MasterConfigInfo();
        info.setConfigVer(dto.getConfigVer());
        info.setTomConfig(dto.getTomConfig());
        info.setAgConfig(dto.getAgConfig());
        info.setTvmConfig(dto.getTvmConfig());
        info.setTrConfig(dto.getTrConfig());
        info.setTicketConfig(dto.getTicketConfig());
        info.setBusinessDayVer(dto.getBusinessDayVer());
        info.setPeakTimeVer(dto.getPeakTimeVer());
        info.setCalenderConfig(dto.getCalenderConfig());
        info.setFareConfig(dto.getFareConfig());
        info.setTopologyConfig(dto.getTopologyConfig());
        info.setUserVer(dto.getUserVer());
        info.setProfileVer(dto.getProfileVer());
        info.setTomSwVer(dto.getTomSwVer());
        info.setAgSwVer(dto.getAgSwVer());
        info.setTvmSwVer(dto.getTvmSwVer());
        info.setTrSwVer(dto.getTrSwVer());
        info.setScSwVer(dto.getScSwVer());
        info.setProductConfig(dto.getProductConfig());
        return info;
    }

    // Domain Model → DTO
    public static MasterConfigInfoDTO masterConfigInfoToDto(MasterConfigInfo info) {
        if (info == null) return null;
        MasterConfigInfoDTO dto = new MasterConfigInfoDTO();
        dto.setConfigVer(info.getConfigVer());
        dto.setTomConfig(info.getTomConfig());
        dto.setAgConfig(info.getAgConfig());
        dto.setTvmConfig(info.getTvmConfig());
        dto.setTrConfig(info.getTrConfig());
        dto.setTicketConfig(info.getTicketConfig());
        dto.setBusinessDayVer(info.getBusinessDayVer());
        dto.setPeakTimeVer(info.getPeakTimeVer());
        dto.setCalenderConfig(info.getCalenderConfig());
        dto.setFareConfig(info.getFareConfig());
        dto.setTopologyConfig(info.getTopologyConfig());
        dto.setUserVer(info.getUserVer());
        dto.setProfileVer(info.getProfileVer());
        dto.setTomSwVer(info.getTomSwVer());
        dto.setAgSwVer(info.getAgSwVer());
        dto.setTvmSwVer(info.getTvmSwVer());
        dto.setTrSwVer(info.getTrSwVer());
        dto.setScSwVer(info.getScSwVer());
        dto.setProductConfig(info.getProductConfig());
        return dto;
    }

    // DTO → Entity
    public static MasterConfigInfoEntity dtoToEntity(MasterConfigInfoDTO dto) {
        if (dto == null) return null;
        MasterConfigInfoEntity entity = new MasterConfigInfoEntity();
        entity.setConfigVer(dto.getConfigVer());
        entity.setTomConfig(dto.getTomConfig());
        entity.setAgConfig(dto.getAgConfig());
        entity.setTvmConfig(dto.getTvmConfig());
        entity.setTrConfig(dto.getTrConfig());
        entity.setTicketConfig(dto.getTicketConfig());
        entity.setBusinessDayVer(dto.getBusinessDayVer());
        entity.setPeakTimeVer(dto.getPeakTimeVer());
        entity.setCalenderConfig(dto.getCalenderConfig());
        entity.setFareConfig(dto.getFareConfig());
        entity.setTopologyConfig(dto.getTopologyConfig());
        entity.setUserVer(dto.getUserVer());
        entity.setProfileVer(dto.getProfileVer());
        entity.setTomSwVer(dto.getTomSwVer());
        entity.setAgSwVer(dto.getAgSwVer());
        entity.setTvmSwVer(dto.getTvmSwVer());
        entity.setTrSwVer(dto.getTrSwVer());
        entity.setScSwVer(dto.getScSwVer());
        entity.setProductConfig(dto.getProductConfig());
        return entity;
    }

    // Entity → Domain Model
    public static MasterConfigInfo entityToMasterConfigInfo(MasterConfigInfoEntity entity) {
        if (entity == null) return null;
        MasterConfigInfo info = new MasterConfigInfo();
        info.setConfigVer(entity.getConfigVer());
        info.setTomConfig(entity.getTomConfig());
        info.setAgConfig(entity.getAgConfig());
        info.setTvmConfig(entity.getTvmConfig());
        info.setTrConfig(entity.getTrConfig());
        info.setTicketConfig(entity.getTicketConfig());
        info.setBusinessDayVer(entity.getBusinessDayVer());
        info.setPeakTimeVer(entity.getPeakTimeVer());
        info.setCalenderConfig(entity.getCalenderConfig());
        info.setFareConfig(entity.getFareConfig());
        info.setTopologyConfig(entity.getTopologyConfig());
        info.setUserVer(entity.getUserVer());
        info.setProfileVer(entity.getProfileVer());
        info.setTomSwVer(entity.getTomSwVer());
        info.setAgSwVer(entity.getAgSwVer());
        info.setTvmSwVer(entity.getTvmSwVer());
        info.setTrSwVer(entity.getTrSwVer());
        info.setScSwVer(entity.getScSwVer());
        info.setProductConfig(entity.getProductConfig());
        return info;
    }

    public static MasterConfigInfoDTO entityToDto(MasterConfigInfoEntity entity) {
        if (entity == null) return null;
        MasterConfigInfoDTO dto = new MasterConfigInfoDTO();
        dto.setConfigVer(entity.getConfigVer());
        dto.setTomConfig(entity.getTomConfig());
        dto.setAgConfig(entity.getAgConfig());
        dto.setTvmConfig(entity.getTvmConfig());
        dto.setTrConfig(entity.getTrConfig());
        dto.setTicketConfig(entity.getTicketConfig());
        dto.setBusinessDayVer(entity.getBusinessDayVer());
        dto.setPeakTimeVer(entity.getPeakTimeVer());
        dto.setCalenderConfig(entity.getCalenderConfig());
        dto.setFareConfig(entity.getFareConfig());
        dto.setTopologyConfig(entity.getTopologyConfig());
        dto.setUserVer(entity.getUserVer());
        dto.setProfileVer(entity.getProfileVer());
        dto.setTomSwVer(entity.getTomSwVer());
        dto.setAgSwVer(entity.getAgSwVer());
        dto.setTvmSwVer(entity.getTvmSwVer());
        dto.setTrSwVer(entity.getTrSwVer());
        dto.setScSwVer(entity.getScSwVer());
        dto.setProductConfig(entity.getProductConfig());
        return dto;
    }

}

