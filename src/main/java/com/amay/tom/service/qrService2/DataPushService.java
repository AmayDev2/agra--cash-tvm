package com.amay.tom.service.qrService2;

import com.amay.tom.agent.Agent;
import com.amay.tom.grpc.scugrpc.ScuDataMapper;
import com.amay.tom.model.LastOnline;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.session.ShiftDto;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.repository.session.ShiftRepository;
import com.amay.tom.repository.session.ShiftRepositoryImpl;
import com.amay.tom.repository.tickets.TicketsRepository;
import com.amay.tom.service.qrService2.push.PushService;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.files.FileSerializeService;
import org.amaytechnosystems.TicketRequestV1;
import org.tinylog.Logger;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataPushService{
    private final PushService pushService;


    public DataPushService(PushService pushService) {
        this.pushService=pushService;
    }

/*    public void pushRemainedDate() {
        // Push the remained data to the server
        LastOnline lastOnline = (LastOnline) FileSerializeService.retrieveData("lastUpdated.ser", EnvFile.getLastUpdatedFile());
        if (lastOnline == null) {
            System.out.println("LastOnline is null");
            return;
        }
        Logger.debug("Last updated time: " + lastOnline.getCCU()+ " " + lastOnline.getSCU());
        // 1- check last updated time
        // Query-> dto -> proto object -> push to server
        //2- push shift information cc
        // 3- push Generated Tickets cc
        ShiftRepository shiftRepository = new ShiftRepositoryImpl(agent.getConnection());
        List<ShiftDto> shiftDtos=shiftRepository.findShiftFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(20))));
        if(shiftDtos.isEmpty()){
            System.out.println("No shift data found");
            return;
        }
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
            if(agent.getPeripheralMonitor().isCcu_connected()) CompletableFuture.runAsync(() -> agent.getCcuService().pushShiftInfo(shift),agent.getThreadPool().getFixedThreadPool());
            if(agent.getPeripheralMonitor().isScu_connected())CompletableFuture.runAsync(() -> agent.getScuService().pushShiftInfo(shift),agent.getThreadPool().getFixedThreadPool());
        }


        TicketsRepository ticketsRepository = agent.getTicketsRepository();
        List<TicketsDto> tickets = ticketsRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
        if (tickets.isEmpty()) {
            System.out.println("No tickets data found");
            return;
        }
        Logger.debug("Tickets data found: {} {}", tickets.toString(), tickets.size());

        for (TicketsDto ticket : tickets) {
            TicketRequestV1 ticketRequestV1 = ScuDataMapper.createTicketIssueRequest(ticket);
            if(agent.getPeripheralMonitor().isCcu_connected())CompletableFuture.runAsync(() -> agent.getCcuService().pushTicketIssueInfo(ticketRequestV1),agent.getThreadPool().getFixedThreadPool());
            if(agent.getPeripheralMonitor().isScu_connected())CompletableFuture.runAsync(() ->agent.getScuService().pushTicketIssueInfo(ticketRequestV1),agent.getThreadPool().getFixedThreadPool());
        }


        // 4- push adjusted, refunded, canceled, replaced on scu
//        AdjustedTicketRepository adjustedTicketRepository = agent.getAdjustedTicketRepository();
//        List<AdjustedTicketDto> adjustedTickets = adjustedTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
//        RefundTicketRepository refundTicketRepository = new RefundTicketRepositoryImpl(agent.getConnection(),agent.getTicketsRepository());
//        List<Refund> refundedTickets = refundTicketRepository.findAllQRTicketsFrom(Timestamp.from(Instant.now().minus(Duration.ofHours(200))));
        //TODO: Add canceled and replaced tickets

        FileSerializeService.saveData("lastUpdated.ser", new LastOnline(Instant.now(),Instant.now()),EnvFile.getLastUpdatedFile());
    }*/

    public void pushData() {
        this.pushService.push();

    }
}
