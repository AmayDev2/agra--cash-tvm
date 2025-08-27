package com.amay.tom.model.analysis;

// ATicketStatusDTO.java
import lombok.Data;

@Data
public class AnalysisTicketStatusDTO {
    private boolean isActive;
    private boolean isRefunded;
    private boolean isReplaced;
    private boolean isExpired;
    private boolean isUsed;
    private boolean isAdjusted;
    private String status;
}
