package com.amay.tom.model.analysis;

// ATicketAnalysisDTO.java
import com.amay.tom.model.adjust.AdjustedTicketDto;
import com.amay.tom.model.refund.RefundDTO;
import lombok.Data;

import java.util.List;

@Data
public class ATicketAnalysisDTO {
    private ATicketDTO ticket;
    private AnalysisTicketStatusDTO ticketStatus;
    private AdjustedTicketDto adjustedTicket;
    private RefundDTO refundTicket;
    private List<ATicketAGStatusDTO> agStatus;
}

