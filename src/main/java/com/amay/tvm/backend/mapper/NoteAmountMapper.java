package com.amay.tvm.backend.mapper;

import com.amay.tvm.backend.dto.NoteAmountDTO;
import com.amay.tvm.backend.entity.NoteAmountEntity;

import java.util.List;
import java.util.stream.Collectors;

public class NoteAmountMapper {

    // Convert Entity → DTO
    public static NoteAmountDTO toDto(NoteAmountEntity entity) {
        if (entity == null) return null;

        return new NoteAmountDTO()
                .setContainerId(entity.getContainerId())
                .setUnitAmount(entity.getUnitAmount())
                .setCashInQuantity(entity.getCashInQuantity())
                .setCashOutQuantity(entity.getCashOutQuantity())
                .setCurrentQuantity(entity.getCashInQuantity() - entity.getCashOutQuantity())
                .setCreatedAt(entity.getCreatedAt())
                .setUpdatedAt(entity.getUpdatedAt());
    }

    // Convert DTO → Entity
    public static NoteAmountEntity toEntity(NoteAmountDTO dto) {
        if (dto == null) return null;

        return new NoteAmountEntity()
                .setContainerId(dto.getContainerId())
                .setUnitAmount(dto.getUnitAmount())
                .setCashInQuantity(dto.getCashInQuantity())
                .setCashOutQuantity(dto.getCashOutQuantity())
                .setCreatedAt(dto.getCreatedAt())
                .setUpdatedAt(dto.getUpdatedAt());
    }

    // Convert List<Entity> → List<DTO>
    public static List<NoteAmountDTO> toDtoList(List<NoteAmountEntity> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(NoteAmountMapper::toDto)
                .collect(Collectors.toList());
    }

    // Convert List<DTO> → List<Entity>
    public static List<NoteAmountEntity> toEntityList(List<NoteAmountDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(NoteAmountMapper::toEntity)
                .collect(Collectors.toList());
    }
}
