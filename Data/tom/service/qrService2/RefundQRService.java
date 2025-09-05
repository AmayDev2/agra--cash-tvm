package com.amay.tom.service.qrService2;

import com.amay.tom.model.refund.RefundDTO;
import java.util.List;

public interface RefundQRService extends QRTicketService {
    void processRefund(RefundDTO refundDTO);
    RefundDTO getRefundDetails(String ticketNumber);
    List<RefundDTO> getRefundsByShift(String shiftId);
    boolean validateRefund(RefundDTO refundDTO);
} 