package com.amay.tom.model.equipment.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


public class EquipmentPrivilege {
    private BooleanProperty qrTicketIssue = new SimpleBooleanProperty();
    private BooleanProperty qrTicketAnalysis = new SimpleBooleanProperty();
    private BooleanProperty qrTicketAdjustment = new SimpleBooleanProperty();



    private BooleanProperty qrTicketCancellation = new SimpleBooleanProperty();
    private BooleanProperty qrTicketRefund = new SimpleBooleanProperty();
    private BooleanProperty qrTicketReprint = new SimpleBooleanProperty();
    private BooleanProperty qrTicketReplacement = new SimpleBooleanProperty();
    private BooleanProperty qrFreeTicket = new SimpleBooleanProperty();
    private BooleanProperty qrPaidTicket = new SimpleBooleanProperty();
    private BooleanProperty tvm = new SimpleBooleanProperty();

    public EquipmentPrivilege(boolean qrTicketIssue, boolean qrTicketAnalysis, boolean qrTicketAdjustment,
                         boolean qrTicketCancellation, boolean qrTicketRefund, boolean qrTicketReprint,
                         boolean qrTicketReplacement, boolean qrFreeTicket, boolean qrPaidTicket, boolean tvm) {
        this.qrTicketIssue.set(qrTicketIssue);
        this.qrTicketAnalysis.set(qrTicketAnalysis);
        this.qrTicketAdjustment.set(qrTicketAdjustment);
        this.qrTicketCancellation.set(qrTicketCancellation);
        this.qrTicketRefund.set(qrTicketRefund);
        this.qrTicketReprint.set(qrTicketReprint);
        this.qrTicketReplacement.set(qrTicketReplacement);
        this.qrFreeTicket.set(qrFreeTicket);
        this.qrPaidTicket.set(qrPaidTicket);
        this.tvm.set(tvm);
    }


    public BooleanProperty isQrTicketIssue() {
        return qrTicketIssue;
    }

    public BooleanProperty qrTicketIssueProperty() {
        return qrTicketIssue;
    }

    public void setQrTicketIssue(boolean qrTicketIssue) {
        this.qrTicketIssue.set(qrTicketIssue);
    }

    public BooleanProperty isQrTicketAnalysis() {
        return qrTicketAnalysis;
    }

    public BooleanProperty qrTicketAnalysisProperty() {
        return qrTicketAnalysis;
    }

    public void setQrTicketAnalysis(boolean qrTicketAnalysis) {
        this.qrTicketAnalysis.set(qrTicketAnalysis);
    }

    public BooleanProperty isQrTicketAdjustment() {
        return qrTicketAdjustment;
    }

    public BooleanProperty qrTicketAdjustmentProperty() {
        return qrTicketAdjustment;
    }

    public void setQrTicketAdjustment(boolean qrTicketAdjustment) {
        this.qrTicketAdjustment.set(qrTicketAdjustment);
    }

    public BooleanProperty isQrTicketCancellation() {
        return qrTicketCancellation;
    }

    public BooleanProperty qrTicketCancellationProperty() {
        return qrTicketCancellation;
    }

    public void setQrTicketCancellation(boolean qrTicketCancellation) {
        this.qrTicketCancellation.set(qrTicketCancellation);
    }

    public BooleanProperty isQrTicketRefund() {
        return qrTicketRefund;
    }

    public BooleanProperty qrTicketRefundProperty() {
        return qrTicketRefund;
    }

    public void setQrTicketRefund(boolean qrTicketRefund) {
        this.qrTicketRefund.set(qrTicketRefund);
    }

    public BooleanProperty isQrTicketReprint() {
        return qrTicketReprint;
    }

    public BooleanProperty qrTicketReprintProperty() {
        return qrTicketReprint;
    }

    public void setQrTicketReprint(boolean qrTicketReprint) {
        this.qrTicketReprint.set(qrTicketReprint);
    }

    public BooleanProperty isQrTicketReplacement() {
        return qrTicketReplacement;
    }

    public BooleanProperty qrTicketReplacementProperty() {
        return qrTicketReplacement;
    }

    public void setQrTicketReplacement(boolean qrTicketReplacement) {
        this.qrTicketReplacement.set(qrTicketReplacement);
    }

    public BooleanProperty isQrFreeTicket() {
        return qrFreeTicket;
    }

    public BooleanProperty qrFreeTicketProperty() {
        return qrFreeTicket;
    }

    public void setQrFreeTicket(boolean qrFreeTicket) {
        this.qrFreeTicket.set(qrFreeTicket);
    }

    public BooleanProperty isQrPaidTicket() {
        return qrPaidTicket;
    }

    public BooleanProperty qrPaidTicketProperty() {
        return qrPaidTicket;
    }

    public void setQrPaidTicket(boolean qrPaidTicket) {
        this.qrPaidTicket.set(qrPaidTicket);
    }

    public BooleanProperty isTvm() {
        return tvm;
    }

    public BooleanProperty tvmProperty() {
        return tvm;
    }

    public void setTvm(boolean tvm) {
        this.tvm.set(tvm);
    }

}
