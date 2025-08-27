package com.amay.tom.model.equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentPrivilegeDto {
    private boolean qrTicketIssue=true;
    private boolean qrTicketAnalysis=true;
    private boolean qrTicketAdjustment=true;
    private boolean qrTicketCancellation=true;
    private boolean qrTicketRefund=true;
    private boolean qrTicketReprint=true;
    private boolean qrTicketReplacement=true;
    private boolean qrFreeTicket=true;
    private boolean qrPaidTicket=true;
    private boolean tvm=true;
}
