package com.amay.tom.model.analysis;

import com.amay.tom.model.adjust.ATicketHistoryDTO;
import com.amay.tom.model.adjust.AdjustedTicketDto;
import com.amay.tom.model.refund.RefundDTO;
import org.amaytechnosystems.*;

import java.util.ArrayList;
import java.util.List;

public class AnalysisTicketMapper {

    public static com.amay.tom.model.analysis.ATicketAnalysisDTO toDTO(ATicketAnalysis proto) {
        com.amay.tom.model.analysis.ATicketAnalysisDTO dto = new ATicketAnalysisDTO();
        dto.setTicket(toDTO(proto.getTicket()));
        dto.setTicketStatus(toDTO(proto.getTicketStatus()));
//        dto.setAdjustedTicket(toDTO(proto.getAdjustedTicket())); -> AdjustmentDetails
        dto.setRefundTicket(toDTO(proto.getRefundTicket()));
        dto.setTicketHistory(toDTO(proto.getHistory()));
        dto.setAdjustmentDetail(toDTO(proto.getAdjustmentDetailsList()));

        List<com.amay.tom.model.analysis.ATicketAGStatusDTO> agStatusList = new ArrayList<>();
        for (ATicketAGStatus status : proto.getAgStatusList()) {
            agStatusList.add(toDTO(status));
        }
        dto.setAgStatus(agStatusList);

        return dto;
    }

    public static ATicketHistoryDTO toDTO(ATicketHistory proto) {
        ATicketHistoryDTO dto = new ATicketHistoryDTO();
        dto.setTicketId(proto.getTicketId());
        dto.setEntryAllowed(proto.getEntryAllowed());
        dto.setEntryCount(proto.getEntryCount());
        dto.setLastEntryTime(proto.hasLastEntryTime() ? proto.getLastEntryTime().getSeconds() : 0L);
        dto.setExitAllowed(proto.getExitAllowed());
        dto.setExitCount(proto.getExitCount());
        dto.setLastExitTime(proto.hasLastExitTime() ? proto.getLastExitTime().getSeconds() : 0L);
        dto.setAdjustCount(proto.getAdjustCount());
        dto.setAdjustedEntryAllowed(proto.getAdjustedEntryAllowed());
        dto.setAdjustedEntryCount(proto.getAdjustedEntryCount());
        dto.setAdjustedExitAllowed(proto.getAdjustedExitAllowed());
        dto.setAdjustedExitCount(proto.getAdjustedExitCount());
        dto.setOvertimeOverride(proto.getOvertimeOverride());
        dto.setOvertravelOverride(proto.getOvertravelOverride());
        return dto;
    }

    public static List<AdjustmentDetailDTO> toDTO(List<AdjustmentDetail> protoList) {
        return protoList.stream()
                .map(adjustmentDetail -> {
                    AdjustmentDetailDTO dto = new AdjustmentDetailDTO();
                    dto.setAdjustId(adjustmentDetail.getAdjustId());
                    dto.setTicketId(adjustmentDetail.getTicketId());
                    dto.setAmount(adjustmentDetail.getAmount());
                    dto.setReason(adjustmentDetail.getReason());
                    dto.setAdjustmentTime(adjustmentDetail.getAdjustmentTime());

                    List<com.amay.tom.enums.AdjustmentType> types = adjustmentDetail.getAdjustmentTypeList().stream()
                            .map(type -> com.amay.tom.enums.AdjustmentType.valueOf(type.name()))
                            .toList();
                    dto.setAdjustmentTypes(types);

                    dto.setAdjustmentArea(adjustmentDetail.getAdjustmentArea());

                    return dto;
                })
                .toList();
    }



    public static com.amay.tom.model.analysis.ATicketDTO toDTO(ATicket proto) {
        com.amay.tom.model.analysis.ATicketDTO dto = new ATicketDTO();
        dto.setTicketNumber(proto.getTicketId());
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
        dto.setTicketType(proto.getProductId());
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

    public static com.amay.tom.model.analysis.ATicketAGStatusDTO toDTO(ATicketAGStatus proto) {
        com.amay.tom.model.analysis.ATicketAGStatusDTO dto = new ATicketAGStatusDTO();
        dto.setTicketNumber(proto.getTicketId());
        dto.setOperation(TicketOperation.valueOf(proto.getOperation().name()));
        dto.setTime(proto.getTime());
        // TODO: Implement device mapping when correct device type is identified
         dto.setDevice(toDTO(proto.getEquipment()));
        return dto;
    }

    public static com.amay.tom.model.analysis.ADeviceDTO toDTO(AEquipment proto) {
        com.amay.tom.model.analysis.ADeviceDTO dto = new ADeviceDTO();
        dto.setDeviceId(proto.getEquipmentId());
//        dto.setLocation(proto.getLocation());
        // Map other fields as needed
        return dto;
    }
}
