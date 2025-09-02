package com.amay.tom.model.replacement;

import java.util.ArrayList;
import java.util.List;

public class ReplacementMapper {

    public static List<ReplacementDTO> toDTO(List<Replacement> Replacements) {
        ArrayList<ReplacementDTO> ReplacementDTOList = new ArrayList<>();
        Replacements.forEach(Replacement -> ReplacementDTOList.add(toDTO((Replacement))));
        return  ReplacementDTOList;
    }
    
    public static ReplacementDTO toDTO(Replacement Replacement) {
        if (Replacement == null) {
            return null;
        }
        
        return new ReplacementDTO(
            Replacement.getTicketNumber(),
            Replacement.getAmount(),
            Replacement.getShiftId(),
            Replacement.getOperatorId(),
            Replacement.getDeviceId(),
            Replacement.getCreationDateTime(),
            Replacement.getUpdateDateTime(),
                Replacement.getTicketType()
        );
    }
    
    public static Replacement toEntity(ReplacementDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new Replacement(
            dto.getTicketNumber(),
            dto.getAmount(),
            dto.getShiftId(),
            dto.getOperatorId(),
            dto.getDeviceId(),
            dto.getCreationDateTime(),
            dto.getUpdateDateTime(),
                dto.getTicketType()
        );
    }
} 