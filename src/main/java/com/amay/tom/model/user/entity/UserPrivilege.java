package com.amay.tom.model.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class  UserPrivilege  implements AutoCloseable{
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

//    public UserPrivilege(boolean qrTicketIssue, boolean qrTicketAnalysis, boolean qrTicketAdjustment,
//                         boolean qrTicketCancellation, boolean qrTicketRefund, boolean qrTicketReprint,
//                         boolean qrTicketReplacement, boolean qrFreeTicket, boolean qrPaidTicket, boolean tvm) {
//        this.qrTicketIssue = qrTicketIssue;
//        this.qrTicketAnalysis = qrTicketAnalysis;
//        this.qrTicketAdjustment = qrTicketAdjustment;
//        this.qrTicketCancellation = qrTicketCancellation;
//        this.qrTicketRefund = qrTicketRefund;
//        this.qrTicketReprint = qrTicketReprint;
//        this.qrTicketReplacement = qrTicketReplacement;
//        this.qrFreeTicket = qrFreeTicket;
//        this.qrPaidTicket = qrPaidTicket;
//        this.tvm = tvm;
//    }

    public boolean isQrTicketIssue() {
        return qrTicketIssue;
    }

    public boolean isQrTicketAnalysis() {
        return qrTicketAnalysis;
    }

    public boolean isQrTicketAdjustment() {
        return qrTicketAdjustment;
    }

    public boolean isQrTicketCancellation() {
        return qrTicketCancellation;
    }

    public boolean isQrTicketRefund() {
        return qrTicketRefund;
    }

    public boolean isQrTicketReprint() {
        return qrTicketReprint;
    }

    public boolean isQrTicketReplacement() {
        return qrTicketReplacement;
    }

    public boolean isQrFreeTicket() {
        return qrFreeTicket;
    }

    public boolean isQrPaidTicket() {
        return qrPaidTicket;
    }

    public boolean isTvm() {
        return tvm;
    }

    @Override
    public void close() throws Exception {

    }
}
