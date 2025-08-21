package com.amay.tom.model.adjust;

import com.amay.tom.model.adjust.AdjustedTicket;
import com.amay.tom.model.adjust.AdjustedTicketDto;

public class AdjustedTicketMapper {

    public static AdjustedTicketDto toDto(AdjustedTicket ticket) {
        if (ticket == null) {
            return null;
        }

        return new AdjustedTicketDto()
                .setOrderId(ticket.getOrderId())
                .setAdjustId(ticket.getAdjustId())
                .setAdjustmentType(ticket.getAdjustmentType())
                .setEncryptedQR(ticket.getEncryptedQR())
                .setIssueTime(ticket.getIssueTime())
                .setEntryTime(ticket.getEntryTime())
                .setExitTime(ticket.getExitTime())
                .setDestination(ticket.getDestination())
                .setTicketNumber(ticket.getTicketNumber())
                .setDeviceId(ticket.getDeviceId())
                .setOperatorId(ticket.getOperatorId())
                .setReason(ticket.getReason())
                .setShiftId(ticket.getShiftId())
                .setArea(ticket.getArea())
                .setPaymentMode(ticket.getPaymentMode())
                .setPenaltyAmount(ticket.getPenaltyAmount())
                .setTransactionId(ticket.getTransactionId())
                .setCreatedAt(ticket.getCreatedAt())
                .setUpdatedAt(ticket.getUpdatedAt())
                .setTransactionTime(ticket.getTransactionTime());
    }

    public static AdjustedTicket toEntity(AdjustedTicketDto dto) {
        if (dto == null) {
            return null;
        }

        return new AdjustedTicket()
                .setOrderId(dto.getOrderId())
                .setAdjustId(dto.getAdjustId())
                .setAdjustmentType(dto.getAdjustmentType())
                .setEncryptedQR(dto.getEncryptedQR())
                .setIssueTime(dto.getIssueTime())
                .setEntryTime(dto.getEntryTime())
                .setExitTime(dto.getExitTime())
                .setDestination(dto.getDestination())
                .setTicketNumber(dto.getTicketNumber())
                .setDeviceId(dto.getDeviceId())
                .setOperatorId(dto.getOperatorId())
                .setReason(dto.getReason())
                .setShiftId(dto.getShiftId())
                .setArea(dto.getArea())
                .setPaymentMode(dto.getPaymentMode())
                .setPenaltyAmount(dto.getPenaltyAmount())
                .setTransactionId(dto.getTransactionId())
                .setCreatedAt(dto.getCreatedAt())
                .setUpdatedAt(dto.getUpdatedAt())
                .setTransactionTime(dto.getTransactionTime());
    }
}
