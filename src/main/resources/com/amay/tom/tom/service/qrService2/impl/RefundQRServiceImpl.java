package com.amay.tom.service.qrService2.impl;

import com.amay.tom.agent.Agent;
import com.amay.tom.model.GeneratedTicket;
import com.amay.tom.model.refund.RefundDTO;
import com.amay.tom.model.refund.RefundMapper;
import com.amay.tom.repository.refund.RefundTicketRepository;
import com.amay.tom.repository.refund.RefundTicketRepositoryImpl;
import com.amay.tom.service.qrService2.RefundQRService;
import com.amay.tom.service.qrService2.TicketInfo;
import com.amay.tom.utils.objects.RefundValidationResponse;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public class RefundQRServiceImpl implements RefundQRService {

    private final Agent agent;
    private final RefundTicketRepository refundTicketRepository;
//    private final TicketsRepositoryImpl ticketsRepository;

    public RefundQRServiceImpl(Agent agent) {
        this.agent = agent;
        this.refundTicketRepository=new RefundTicketRepositoryImpl(agent.getConnection(), agent.getTicketsRepository());
//        this.ticketsRepository = new TicketsRepositoryImpl(agent.getConnection());
    }

    @Override
    public void processRefund(RefundDTO refundDTO)  {
        try {
                refundDTO.setDeviceId(agent.getSystemConfig().getCurrentEquipment().getEquipmentId());
                refundDTO.setOperatorId(agent.getShift().getOperatorId());
                refundDTO.setShiftId(agent.getShift().getShiftId());
                this.refundTicketRepository.insert(RefundMapper.toEntity(refundDTO));
//                TicketsDto ticketsDto = this.ticketsRepository.findById(refundDTO.getTicketNumber());
//                ticketsDto.setActive(false);
//                ticketsDto.setRefund(true);
//                this.ticketsRepository.update(ticketsDto);
                Logger.info("Refund processed successfully for ticket: {}", refundDTO.getTicketNumber());
        } catch (Exception e) {
            Logger.error("Error processing refund: {}", e.getMessage());
        }
    }

    @Override
    public RefundDTO getRefundDetails(String ticketNumber) {
        try {
            return RefundMapper.toDTO(this.refundTicketRepository.findByTicketNumber(ticketNumber));
        } catch (Exception e) {
            Logger.error("Error getting refund details: {}", e.getMessage());
            throw new RuntimeException("Failed to get refund details", e);
        }
    }

    @Override
    public List<RefundDTO> getRefundsByShift(String shiftId) {
        try {
            return RefundMapper.toDTO(this.refundTicketRepository.findByShiftId(shiftId));
        } catch (Exception e) {
            Logger.error("Error getting refunds by shift: {}", e.getMessage());
            throw new RuntimeException("Failed to get refunds by shift", e);
        }
    }

    @Override
    public RefundValidationResponse validateRefund(RefundDTO refundDTO) {
        RefundValidationResponse validationResponse =null;
        try {

            // Check if ticket exists and hasn't been refunded already
            RefundDTO existingRefund = getRefundDetails(refundDTO.getTicketNumber());
            if (existingRefund != null) {
                Logger.error("Ticket already refunded: {}", refundDTO.getTicketNumber());
                throw new RuntimeException("Ticket already refunded: " + refundDTO.getTicketNumber());
            }

            // Add any additional validation rules here
            // For example:
            // - Check if ticket is within refund window
            // - Validate amount
            // - Check operator permissions
            // - Verify device status

           validationResponse= new RefundValidationResponse(true, "Refund is valid");
        } catch (Exception e) {
            Logger.error("Error validating refund: {}", e.getMessage());
            validationResponse=new RefundValidationResponse(false, e.getMessage());
        }
        return validationResponse;



    }

    @Override
    public ArrayList<GeneratedTicket> processTicket(String OrderId, String transactionId, TicketInfo ticketInfo) {
        return null;
    }
}