package com.amay.tom.service.base36;

import com.amay.tom.repository.session.ShiftRepository;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketIdGeneratorService {

    private final String shiftId;
    private AtomicInteger orderCounter;


    public TicketIdGeneratorService( String shift) {
        if ( shift == null || shift.isEmpty()) {
            throw new IllegalArgumentException("ShiftId must not be null or empty");
        }
        this.shiftId=shift;
        this.orderCounter = new AtomicInteger(0);
    }


    public String generateTicketId() {
        int orderNumber = orderCounter.incrementAndGet();
        return String.format("%s%04d", shiftId, orderNumber);
    }
}
