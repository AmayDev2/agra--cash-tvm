package com.amay.tom.service.cancel.impl;

import com.amay.tom.agent.Agent;
import com.amay.tom.model.tickets.TicketsDto;
import com.amay.tom.service.cancel.ICancelService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.tinylog.Logger;

import java.sql.Connection;

public class CancelService implements ICancelService {
    private Connection connection;
    private final com.amay.tom.repository.tickets.TicketsRepository ticketsRepository;
    private final ObservableList<TicketsDto> ticketList;

    public CancelService( Agent agent) {
        // Private constructor to prevent instantiation
        ticketsRepository = agent.getTicketsRepository();
        ticketList= FXCollections.observableArrayList();
    }
    @Override
    public boolean markTicketCancelByOrderId(String orderId) {
        //System.out.println("Order id : "+orderId);
        ticketsRepository.markTicketCancelByOrderId(orderId);
        Logger.debug("Marked ticket as cancel for order id {}",orderId);
//        agent.getScuService().markTicketCancelByOrderId(orderId);
        return false;
    }

    @Override
    public void getCancelTicketInfo(String orderId) {
        //System.out.println("getCancelTicketInfo Order id : "+orderId);
        ticketList.addAll(ticketsRepository.getTicketByOrderId(orderId));
    }

    @Override
    public ObservableList<TicketsDto> getObserverList() {
        return ticketList;
    }

}
