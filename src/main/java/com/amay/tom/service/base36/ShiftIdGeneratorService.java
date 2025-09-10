package com.amay.tom.service.base36;

import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.service.base36.OrderIdGeneratorService;
import com.amay.tom.service.base36.TicketIdGeneratorService;
import lombok.Data;
import org.tinylog.Logger;

import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ShiftIdGeneratorService {

    private final String shiftId;
    private com.amay.tom.service.base36.OrderIdGeneratorService orderIdGeneratorService;
    private TicketIdGeneratorService ticketIdGeneratorService;

    //
    public ShiftIdGeneratorService(int serverLastShiftSeq,ShiftRepository shiftRepository, String currentDayPrefix) throws SQLException {
        if (shiftRepository == null || currentDayPrefix == null || currentDayPrefix.isEmpty()) {
            throw new IllegalArgumentException("ShiftRepository and currentDayPrefix must not be null or empty");
        }

        AtomicInteger count=new AtomicInteger();
        Optional<String> shiftId=shiftRepository.findLastShift();
        shiftId.ifPresentOrElse(
                id -> {
                    count.set(Integer.parseInt(id.substring(id.length()-2)));
                },
                () -> {
                    // do something when not present
                    Logger.debug("No shift found");
                }
        );



//        AtomicInteger count = new AtomicInteger(shiftRepository.countShiftIdsWithPrefix(currentDayPrefix));
        count.set(Math.max(count.get(),serverLastShiftSeq));
        if(count.get() >= 99) {
            throw new SQLException("Shift ID limit reached for the day: " + currentDayPrefix);
        }
        //System.out.println("Shift Id: " + count);
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
