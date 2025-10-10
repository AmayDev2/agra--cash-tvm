package com.amay.tom.model.session;


import com.amay.tom.model.session.Shift;
import com.amay.tom.model.session.ShiftDto;

import java.sql.Timestamp;


import java.sql.Timestamp;

public class ShiftMapper {
    public static com.amay.tom.model.session.Shift toModel(com.amay.tom.model.session.ShiftDto dto) {
        return new com.amay.tom.model.session.Shift(
                dto.getShiftId(),
                dto.getOperatorId(),
                dto.getDeviceId(),
                dto.getDeviceSerial(),
                dto.getCreatedAt() != null ? dto.getCreatedAt().toLocalDateTime() : null,
                dto.getStartTime() != null ? dto.getStartTime().toLocalDateTime() : null,
                dto.getEndTime() != null ? dto.getEndTime().toLocalDateTime() : null,
                dto.getSerialNo(),
                dto.getStationId(),
                dto.getLineNo(),
                dto.getReason(),
                dto.getCurrentStatus(),
                dto.getUpdatedAt() != null ? dto.getUpdatedAt().toLocalDateTime() : null,
                dto.getImprest_money(),
                dto.getConfig_version(),
                dto.isCcu(),
                dto.isScu(),
                dto.getRole()
        );
    }

    public static com.amay.tom.model.session.ShiftDto toDto(Shift shift) {
        com.amay.tom.model.session.ShiftDto dto = new ShiftDto()
                .setShiftId(shift.getShiftId())
                .setOperatorId(shift.getOperatorId())
                .setDeviceId(shift.getDeviceId())
                .setDeviceSerial(shift.getDeviceSerial())
                .setCreatedAt(shift.getCreatedAt() != null ? Timestamp.valueOf(shift.getCreatedAt()) : null)
                .setStartTime(shift.getStartTime() != null ? Timestamp.valueOf(shift.getStartTime()) : null)
                .setEndTime(shift.getEndTime() != null ? Timestamp.valueOf(shift.getEndTime()) : null)
                .setSerialNo(shift.getSerialNo())
                .setStationId(shift.getStationId())
                .setLineNo(shift.getLineNo())
                .setReason(shift.getReason())
                .setCurrentStatus(shift.getCurrentStatus())
                .setUpdatedAt(shift.getUpdatedAt() != null ? Timestamp.valueOf(shift.getUpdatedAt()) : null)
                .setImprest_money(shift.getImprest_money())
                .setConfig_version(shift.getConfig_version())
                .setCcu(shift.isCcu())
                .setRole(shift.getRole())
                .setScu(shift.isScu());
        return dto;
    }
}
