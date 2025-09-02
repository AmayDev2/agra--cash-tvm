package com.amay.tom.model.analysis;

// ATicketAnalysisDTO.java

import com.amay.tom.model.adjust.ATicketHistoryDTO;
import com.amay.tom.model.adjust.AdjustedTicketDto;
import com.amay.tom.model.analysis.ATicketAGStatusDTO;
import com.amay.tom.model.analysis.ATicketDTO;
import com.amay.tom.model.analysis.AdjustmentDetailDTO;
import com.amay.tom.model.analysis.AnalysisTicketStatusDTO;
import com.amay.tom.model.refund.RefundDTO;
import lombok.Data;

import java.util.List;

@Data
public class ATicketAnalysisDTO {
    private ATicketDTO ticket;
    private AnalysisTicketStatusDTO ticketStatus;
    private AdjustedTicketDto adjustedTicket;
    private RefundDTO refundTicket;
    private ATicketHistoryDTO ticketHistory;
    private List<ATicketAGStatusDTO> agStatus;
    private List<AdjustmentDetailDTO> adjustmentDetail;
}

