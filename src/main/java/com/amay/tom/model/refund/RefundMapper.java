package com.amay.tom.model.refund;

import java.util.ArrayList;
import java.util.List;

public class RefundMapper {

    public static List<RefundDTO> toDTO(List<Refund> refunds) {
        ArrayList<RefundDTO> refundDTOList = new ArrayList<>();
        refunds.forEach(refund -> refundDTOList.add(toDTO((refund))));
        return  refundDTOList;
    }
    
    public static RefundDTO toDTO(Refund refund) {
        if (refund == null) {
            return null;
        }
        
        return new RefundDTO(
            refund.getTicketNumber(),
            refund.getAmount(),
            refund.getShiftId(),
            refund.getOperatorId(),
            refund.getRefundMode(),
            refund.getDeviceId(),
            refund.getCreationDateTime(),
            refund.getUpdateDateTime()
        );
    }
    
    public static Refund toEntity(RefundDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new Refund(
            dto.getTicketNumber(),
            dto.getAmount(),
            dto.getShiftId(),
            dto.getOperatorId(),
            dto.getRefundMode(),
            dto.getDeviceId(),
            dto.getCreationDateTime(),
            dto.getUpdateDateTime()
        );
    }
} 