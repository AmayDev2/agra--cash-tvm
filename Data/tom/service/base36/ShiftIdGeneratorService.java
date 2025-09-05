package com.amay.tom.service.base36;

import com.amay.tom.repository.session.ShiftRepository;
import lombok.Data;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ShiftIdGeneratorService {

    private final String shiftId;
    private OrderIdGeneratorService orderIdGeneratorService;
    private TicketIdGeneratorService ticketIdGeneratorService;

    //
    public ShiftIdGeneratorService(ShiftRepository shiftRepository, String currentDayPrefix) throws SQLException {
        if (shiftRepository == null || currentDayPrefix == null || currentDayPrefix.isEmpty()) {
            throw new IllegalArgumentException("ShiftRepository and currentDayPrefix must not be null or empty");
        }

        AtomicInteger count = new AtomicInteger(shiftRepository.countShiftIdsWithPrefix(currentDayPrefix));
        if(count.get() >= 99) {
            throw new SQLException("Shift ID limit reached for the day: " + currentDayPrefix);
        }
        System.out.println("Shift Id: " + count);
        this.shiftId = currentDayPrefix+String.format("%02d", count.addAndGet(1));
    }

//    public String getShiftId() {
//        return shiftId;
//    }
//
    public void setOrderIdGeneratorService() {
       orderIdGeneratorService= new OrderIdGeneratorService(shiftId);
    }
    public void setTicketIdGeneratorService() {
        ticketIdGeneratorService= new TicketIdGeneratorService(shiftId);
    }
//
}
