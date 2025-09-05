package com.amay.tom.model.analysis;

import com.amay.tom.model.adjust.AdjustedTicketDto;
import com.amay.tom.model.refund.RefundDTO;
import com.amay.tom.service.analysis.Analysis;
import org.amaytechnosystems.*;

import java.util.ArrayList;
import java.util.List;

public class AnalysisTicketMapper {

    public static ATicketAnalysisDTO toDTO(ATicketAnalysis proto) {
        ATicketAnalysisDTO dto = new ATicketAnalysisDTO();
        dto.setTicket(toDTO(proto.getTicket()));
        dto.setTicketStatus(toDTO(proto.getTicketStatus()));
        dto.setAdjustedTicket(toDTO(proto.getAdjustedTicket()));
        dto.setRefundTicket(toDTO(proto.getRefundTicket()));

        List<ATicketAGStatusDTO> agStatusList = new ArrayList<>();
        for (ATicketAGStatus status : proto.getAgStatusList()) {
            agStatusList.add(toDTO(status));
        }
        dto.setAgStatus(agStatusList);

        return dto;
    }

    public static ATicketDTO toDTO(ATicket proto) {
        ATicketDTO dto = new ATicketDTO();
        dto.setTicketNumber(proto.getTicketNumber());
        dto.setOrderId(proto.getOrderId());
        dto.setTransactionId(proto.getTransactionId());
        dto.setTransactionUk(proto.getTransactionUk());
        dto.setTicketIssue(proto.getTicketIssue());
        dto.setTicketExp(proto.getTicketExp());
        dto.setSourceStation(proto.getSourceStation());
        dto.setDestinationStation(proto.getDestinationStation());
        dto.setLanguage(proto.getLanguage());
        dto.setAmount(proto.getAmount());
        dto.setDiscount(proto.getDiscount());
        dto.setActive(proto.getIsActive());
        dto.setTicketType(proto.getTicketType());
        dto.setQuantity(proto.getQuantity());
        dto.setStatus(proto.getStatus());
        dto.setPaymentMode(proto.getPaymentMode());
        dto.setQrData(proto.getQrData());
        dto.setAdjusted(proto.getIsAdjusted());
        return dto;
    }

    public static AnalysisTicketStatusDTO toDTO(ATicketStatus proto) {
        AnalysisTicketStatusDTO dto = new AnalysisTicketStatusDTO();
        dto.setActive(proto.getIsActive());
        dto.setRefunded(proto.getIsRefunded());
        dto.setReplaced(proto.getIsReplaced());
        dto.setExpired(proto.getIsExpired());
        dto.setUsed(proto.getIsUsed());
        dto.setAdjusted(proto.getIsAdjusted());
        dto.setStatus(proto.getStatus());
        return dto;
    }

    public static AdjustedTicketDto toDTO(AdjustedTicket proto) {
        // Implement mapping as per AdjustedTicket fields
        return new AdjustedTicketDto();
    }

    public static RefundDTO toDTO(ARefundTicket proto) {
        // Implement mapping as per ARefundTicket fields
        return new RefundDTO();
    }

    public static ATicketAGStatusDTO toDTO(ATicketAGStatus proto) {
        ATicketAGStatusDTO dto = new ATicketAGStatusDTO();
        dto.setTicketNumber(proto.getTicketNumber());
        dto.setOperation(TicketOperation.valueOf(proto.getOperation().name()));
        dto.setTime(proto.getTime());
        dto.setDevice(toDTO(proto.getDevice()));
        return dto;
    }

    public static ADeviceDTO toDTO(ADevice proto) {
        ADeviceDTO dto = new ADeviceDTO();
        dto.setDeviceId(proto.getDeviceId());
//        dto.setLocation(proto.getLocation());
        // Map other fields as needed
        return dto;
    }
}
