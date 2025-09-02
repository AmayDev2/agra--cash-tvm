package com.amay.tom.model.refund;

import com.amay.tom.model.refund.Refund;
import com.amay.tom.model.refund.RefundDTO;

import java.util.ArrayList;
import java.util.List;

public class RefundMapper {

    public static List<com.amay.tom.model.refund.RefundDTO> toDTO(List<com.amay.tom.model.refund.Refund> refunds) {
        ArrayList<com.amay.tom.model.refund.RefundDTO> refundDTOList = new ArrayList<>();
        refunds.forEach(refund -> refundDTOList.add(toDTO((refund))));
        return  refundDTOList;
    }
    
    public static com.amay.tom.model.refund.RefundDTO toDTO(com.amay.tom.model.refund.Refund refund) {
        if (refund == null) {
            return null;
        }
        
        return new com.amay.tom.model.refund.RefundDTO(
                refund.getTicketNumber(),
                refund.getAmount(),
                refund.getShiftId(),
                refund.getOperatorId(),
                refund.getRefundMode(),
                refund.getDeviceId(),
                refund.getCreationDateTime(),
                refund.getUpdateDateTime(),
                refund.getTicketType(),
                refund.getRefundId(),
                refund.getStatus()
        );
    }
    
    public static com.amay.tom.model.refund.Refund toEntity(RefundDTO dto) {
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
                dto.getUpdateDateTime(),
                dto.getTicketType(),
                dto.getRefundId(),
                dto.getStatus(),
                dto.isCcu(),   // ✅ new
                dto.isScu()    // ✅ new
        );
    }
} 