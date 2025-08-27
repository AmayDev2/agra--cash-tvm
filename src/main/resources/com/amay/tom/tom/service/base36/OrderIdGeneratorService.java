package com.amay.tom.service.base36;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderIdGeneratorService {

    private final String shiftId;
    private AtomicInteger orderCounter;
    private final String ORDER_ID_PREFIX = "ORD";


    public OrderIdGeneratorService( String shift) {
        if ( shift == null || shift.isEmpty() ) {
            throw new IllegalArgumentException("ShiftRepository and shiftId must not be null or empty");
        }
        this.shiftId=shift;
        this.orderCounter = new AtomicInteger(0);
    }


    public String generateOrderId() {
        int orderNumber = orderCounter.incrementAndGet();
        return String.format("%s%s%04d", ORDER_ID_PREFIX, shiftId, orderNumber);
    }
}
