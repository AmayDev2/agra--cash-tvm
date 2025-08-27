package com.amay.tom.service.refund;

import com.amay.tom.model.refund.RefundDTO;

import java.util.List;

public interface RefundService {
    void createRefund(RefundDTO refundDTO);
    RefundDTO getRefundByTicketNumber(String ticketNumber);
    List<RefundDTO> getRefundsByShiftId(String shiftId);
} 