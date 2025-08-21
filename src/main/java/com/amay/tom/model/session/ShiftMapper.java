package com.amay.tom.model.session;


import java.sql.Timestamp;


import java.sql.Timestamp;

public class ShiftMapper {
    public static Shift toModel(ShiftDto dto) {
        return new Shift(
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
                dto.getUpdatedAt() != null ? dto.getUpdatedAt().toLocalDateTime() : null
        );
    }

    public static ShiftDto toDto(Shift shift) {
        ShiftDto dto = new ShiftDto()
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
                .setUpdatedAt(shift.getUpdatedAt() != null ? Timestamp.valueOf(shift.getUpdatedAt()) : null);
        return dto;
    }
}
