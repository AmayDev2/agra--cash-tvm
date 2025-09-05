package com.amay.tom.service.qrService2.push;

import com.amay.tom.agent.Agent;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.model.LastOnline;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.session.ShiftDto;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.repository.session.ShiftRepositoryImpl;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.files.FileSerializeService;
import org.amaytechnosystems.TicketRequestV1;
import org.tinylog.Logger;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class CCUPushService implements PushService {
    private final Agent agent;
    public CCUPushService(Agent agent){
        this.agent=agent;

    }
    @Override
    public void push() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        // Push the remained data to the server
        LastOnline lastOnline = (LastOnline) FileSerializeService.retrieveData("lastUpdated.ser", EnvFile.getLastUpdatedFile());
        if (lastOnline == null) {
            Logger.debug("LastOnline is null");
            lastOnline = new LastOnline().setCCU(Instant.now().minus(Duration.ofDays(30))).setSCU(Instant.now().minus(Duration.ofDays(30)));
        }
        Logger.debug("Last updated time: " + lastOnline.getCCU()+ " " + lastOnline.getSCU());
        // 1- check last updated time
        // Query-> dto -> proto object -> push to server
        //2- push shift information cc
        // 3- push Generated Tickets cc
        ShiftRepository shiftRepository = new ShiftRepositoryImpl(agent.getConnection());
        List<ShiftDto> shiftDtos=shiftRepository.findShiftFrom(Timestamp.from(lastOnline.getCCU()));
        if(shiftDtos.isEmpty()){
            Logger.debug("No shift data found");
            return;
        }
        ArrayList<CompletableFuture<Void>> futures = new ArrayList<>();
        Logger.debug("Shift data found: {} {}" ,shiftDtos.toString(),shiftDtos.size());
        for(ShiftDto dbShift: shiftDtos){
            Shift shift = new Shift()
                    .setOperatorId(dbShift.getOperatorId())
                    .setShiftId(dbShift.getShiftId())
                    .setDeviceId(dbShift.getDeviceId())
                    .setDeviceSerial(dbShift.getSerialNo())
                    .setCreatedAt(dbShift.getCreatedAt().toLocalDateTime())
                    .setStartTime(dbShift.getStartTime().toLocalDateTime())
                    .setUpdatedAt(dbShift.getUpdatedAt().toLocalDateTime())
                    .setStationId(dbShift.getStationId())
                    .setLineNo(dbShift.getLineNo())
                    .setCurrentStatus(dbShift.getCurrentStatus());
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    agent.getCcuService().pushShiftInfoBulk(shift);
                    isSuccess.set(true);
                } catch (Exception e) {
                    Logger.debug("Error pushing shift info to CCU: {}", e.getMessage());
                }
            },agent.getThreadPool().getFixedThreadPool()));
//            if(agent.getPeripheralMonitor().isScu_connected())CompletableFuture.runAsync(() -> agent.getScuService().pushShiftInfo(shift),agent.getThreadPool().getFixedThreadPool());
        }


        TicketsRepository ticketsRepository = agent.getTicketsRepository();
        List<TicketsDto> tickets = ticketsRepository.findAllQRTicketsFrom(Timestamp.from(lastOnline.getCCU()));
        if (tickets.isEmpty()) {
            Logger.debug("No tickets data found");
            return;
        }
        Logger.debug("Tickets data found: {} {}", tickets.toString(), tickets.size());

        for (TicketsDto ticket : tickets) {
            TicketRequestV1 ticketRequestV1 = ScuDataMapper.createTicketIssueRequest(ticket);
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    agent.getCcuService().pushTicketIssueInfoBulk(ticketRequestV1);
                    isSuccess.set(true);
                } catch (Exception e) {
                    Logger.error("Error pushing ticket issue info to CCU: {}", e.getMessage());
                }
            },agent.getThreadPool().getFixedThreadPool()));
//            if(agent.getPeripheralMonitor().isScu_connected())CompletableFuture.runAsync(() ->agent.getScuService().pushTicketIssueInfo(ticketRequestV1),agent.getThreadPool().getFixedThreadPool());
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 4- push adjusted, refunded, canceled, replaced on scu
//        AdjustedTicketRepository adjustedTicketRepository = agent.getAdjustedTicketRepository();
//        List<AdjustedTicketDto> adjustedTickets = adjustedTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
//        RefundTicketRepository refundTicketRepository = new RefundTicketRepositoryImpl(agent.getConnection(),agent.getTicketsRepository());
//        List<Refund> refundedTickets = refundTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
        //TODO: Add canceled and replaced tickets

        if(isSuccess.get())FileSerializeService.saveData("lastUpdated.ser", lastOnline.setCCU(Instant.now()),EnvFile.getLastUpdatedFile());


    }
}
