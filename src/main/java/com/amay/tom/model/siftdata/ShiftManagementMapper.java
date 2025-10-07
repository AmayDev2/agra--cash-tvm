package com.amay.tom.model.siftdata;

import com.amay.tom.model.session.ShiftDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public class ShiftManagementMapper {
    public static ShiftManagementDTO fromDto(ShiftDto dto) {
        if (dto == null) {
            return null;
        }

        ShiftManagementDTO target = new ShiftManagementDTO();
        target.setShiftId(dto.getShiftId());
        target.setOperatorId(dto.getOperatorId());
        target.setStartTime(dto.getStartTime().toLocalDateTime());
        target.setEndTime(dto.getEndTime().toLocalDateTime());

        // Copy selected if needed (currently ignored in your earlier request)
        // target.getSelected().set(dto.getSelected().get());

        return target;
    }
}
