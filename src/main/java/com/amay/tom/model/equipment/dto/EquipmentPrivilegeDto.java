package com.amay.tom.model.equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentPrivilegeDto {
    private boolean qrTicketIssue;
    private boolean qrTicketAnalysis;
    private boolean qrTicketAdjustment;
    private boolean qrTicketCancellation;
    private boolean qrTicketRefund;
    private boolean qrTicketReprint;
    private boolean qrTicketReplacement;
    private boolean qrFreeTicket;
    private boolean qrPaidTicket;
    private boolean tvm;
}
