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

public class SCUPushService implements PushService{
    private final Agent agent;

    public SCUPushService(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void push() {
        AtomicBoolean isSuccess= new AtomicBoolean(false);
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
        List<ShiftDto> shiftDtos=shiftRepository.findShiftFrom(Timestamp.from(lastOnline.getSCU()));
        if(shiftDtos.isEmpty()){
            Logger.debug("No shift data found");
        }else {
            Logger.debug("Shift data found: {} {}", shiftDtos.toString(), shiftDtos.size());
        }
        List<CompletableFuture<Void>> futures = new ArrayList<>();
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
//            if(agent.getPeripheralMonitor().isCcu_connected()) CompletableFuture.runAsync(() -> agent.getCcuService().pushShiftInfo(shift),agent.getThreadPool().getFixedThreadPool());
           futures.add( CompletableFuture.runAsync(() -> {
                try {
                    agent.getScuService().pushShiftInfoBulk(shift);
                     isSuccess.set(true);

                } catch (Exception e) {
                    Logger.error("Error pushing shift info to SCU: {}", e.getMessage(), e);
                }
            },agent.getThreadPool().getFixedThreadPool()));
        }


        TicketsRepository ticketsRepository = agent.getTicketsRepository();
        List<TicketsDto> tickets = ticketsRepository.findAllQRTicketsFrom(Timestamp.from(lastOnline.getSCU()));
        if (tickets.isEmpty()) {
            Logger.debug("No tickets data found");
        }else {
            Logger.debug("Tickets data found: {} {}", tickets.toString(), tickets.size());
        }



        for (TicketsDto ticket : tickets) {
            TicketRequestV1 ticketRequestV1 = ScuDataMapper.createTicketIssueRequest(ticket);
//            if(agent.getPeripheralMonitor().isCcu_connected())CompletableFuture.runAsync(() -> agent.getCcuService().pushTicketIssueInfo(ticketRequestV1),agent.getThreadPool().getFixedThreadPool());
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    agent.getScuService().pushTicketIssueInfoBulk(ticketRequestV1);
                    isSuccess.set(true);
                } catch (Exception e) {
                    Logger.error("Error pushing ticket issue info to SCU: {}", e.getMessage(), e);
                }
            },agent.getThreadPool().getFixedThreadPool()));


        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        Logger.debug("All push operations completed successfully: {}", isSuccess.get());






        // 4- push adjusted, refunded, canceled, replaced on scu
//        AdjustedTicketRepository adjustedTicketRepository = agent.getAdjustedTicketRepository();
//        List<AdjustedTicketDto> adjustedTickets = adjustedTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
//        RefundTicketRepository refundTicketRepository = new RefundTicketRepositoryImpl(agent.getConnection(),agent.getTicketsRepository());
//        List<Refund> refundedTickets = refundTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
        //TODO: Add canceled and replaced tickets
        if(isSuccess.get())FileSerializeService.saveData("lastUpdated.ser", lastOnline.setSCU(Instant.now()),EnvFile.getLastUpdatedFile());



    }
}
