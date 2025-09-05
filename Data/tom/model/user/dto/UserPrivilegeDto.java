package com.amay.tom.model.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserPrivilegeDto {
    private String username;
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
    private boolean importAndExport;
    private boolean shutdownAndRestart;
}
