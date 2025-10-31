package com.amay.tvm.backend.service.DataSync;

import com.amay.tvm.backend.enums.DataSyncDestination;
import com.amay.tom.repository.adjustment.AdjustedTicketRepository;
import com.amay.tom.repository.refund.RefundTicketRepository;
import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tvm.util.DataSync.DataSyncInfo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;

public class DataSyncCountService {


    private TicketsRepository ticketsRepository;
    private ShiftRepository shiftRepository;

    public DataSyncCountService( TicketsRepository ticketsRepository, ShiftRepository shiftRepository) {
        this.ticketsRepository = ticketsRepository;
        this.shiftRepository = shiftRepository;
    }

    public void updateSyncCount(){
        refreshTotalDataCount();
        refreshOnlineDataCount(DataSyncDestination.CCU);
        refreshOfflineDataCount(DataSyncDestination.CCU);
        refreshLastSyncTimestamp(DataSyncDestination.CCU);
        refreshOnlineDataCount(DataSyncDestination.SCU);
        refreshOfflineDataCount(DataSyncDestination.SCU);
        refreshLastSyncTimestamp(DataSyncDestination.SCU);
    }

    /**
     * Refreshes the count of total data
     */

    private void refreshTotalDataCount() {
        long totalTickets = ticketsRepository.findTotalRowsCount();
        long totalShifts = shiftRepository.findTotalRowsCount();

        DataSyncInfo.totalDataCount =  totalTickets + totalShifts;
    }

    /**
     * Refreshes the count of online (synced) data for the given destination.
     */
    private void refreshOnlineDataCount(DataSyncDestination destination) {

        long ticketPushed = ticketsRepository.findPushedRowsCount(destination);
        long shiftPushed = shiftRepository.findPushedRowsCount(destination);

        long totalOnline =  ticketPushed + shiftPushed;

        switch (destination) {
            case CCU -> DataSyncInfo.onlineDataCountCCU = totalOnline;
            case SCU -> DataSyncInfo.onlineDataCountSCU = totalOnline;
        }
    }

    /**
     * Refreshes the count of offline (not yet synced) data for the given destination.
     */
    private void refreshOfflineDataCount(DataSyncDestination destination) {
        switch (destination) {
            case CCU ->
                    DataSyncInfo.offlineDataCountCCU = DataSyncInfo.totalDataCount - DataSyncInfo.onlineDataCountCCU;
            case SCU ->
                    DataSyncInfo.offlineDataCountSCU = DataSyncInfo.totalDataCount - DataSyncInfo.onlineDataCountSCU;
        }
    }

    /**
     * Refreshes the last sync timestamp (formatted).
     */
    private void refreshLastSyncTimestamp(DataSyncDestination destination) {
        Timestamp maxTime = Stream.of(
                        shiftRepository.findLastPushedTimeStamp(destination),
                        ticketsRepository.findLastPushedTimeStamp(destination)

                )
                .filter(Objects::nonNull)       // ignore nulls (no data)
                .max(Timestamp::compareTo)      // get the latest timestamp
                .orElse(null);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        switch (destination) {
            case CCU ->
                    DataSyncInfo.lastSyncCCU = maxTime!=null ? maxTime.toLocalDateTime().format(formatter) : LocalDateTime.now().format(formatter);
            case SCU ->
                    DataSyncInfo.lastSyncSCU = maxTime!=null ? maxTime.toLocalDateTime().format(formatter) : LocalDateTime.now().format(formatter);
        }


    }
}
