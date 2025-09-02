package com.amay.tom.model.analysis;

// ATicketAGStatusDTO.java
import com.amay.tom.model.analysis.ADeviceDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.amaytechnosystems.TicketOperation;

@Data
@RequiredArgsConstructor
public class ATicketAGStatusDTO {
    private String ticketNumber;
    private TicketOperation operation;
    private ADeviceDTO device;
    private String time;
}
