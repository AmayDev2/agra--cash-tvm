package com.amay.tom.service.controller.impl;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.Language;
import com.amay.tom.exceptions.NotFoundStationException;
import com.amay.tom.exceptions.TicketNotAddedException;
import com.amay.tom.exceptions.TicketNotFoundException;
import com.amay.tom.model.MetroTicket;
import com.amay.tom.model.Passenger;
import com.amay.tom.model.station.Station;
import com.amay.tom.model.TicketType;
import com.amay.tom.repository.StationData;
import com.amay.tom.repository.TicketTypeData;
import com.amay.tom.service.chield.TicketService;
import com.amay.tom.service.chield.ticketservice.ImplTicketService;
import com.amay.tom.service.controller.MainUIService;
import org.tinylog.Logger;

import java.util.ArrayList;

public class ImplMainUiService implements MainUIService {

//    @Getter
//    private final MainController mainController;

    private final ArrayList<Passenger> passengerList;

    private final TicketService ticketService ;

    public ImplMainUiService() {
//        this.mainController = mainController;
        passengerList = new ArrayList<>();
        ticketService = new ImplTicketService();
    }

    public Station[] getStationDetails() throws NotFoundStationException {
        return StationData.getInstance().getStationArray();
    }

    public TicketType[] getTicketTypeDetails() throws TicketNotFoundException {
        return TicketTypeData.getInstance().getTicketTypeArray();
    }

    @Override
    public void addTicket(Station selectedDestination, int selectedNoOfPassenger, TicketType selectedTicketType, Language language) throws TicketNotAddedException {
        Logger.debug("Adding ticket to the list, size : {}", passengerList.size());

//        if (selectedDestination == null || selectedNoOfPassenger <= 0 || selectedTicketType == null || language == null) {
//            throw new TicketNotAddedException("Ticket not added");
//        }
        Passenger passenger = new Passenger(SystemConfig.getInstance().getCurrentStation(), selectedDestination, selectedNoOfPassenger, selectedTicketType, language);

        Logger.debug("Passenger : {}", passenger);

        passengerList.add(passenger);

    }

    @Override
    public ArrayList<MetroTicket> ConfirmTicket(Station selectedDestination, int selectedNoOfPassenger, TicketType selectedTicketType) throws RuntimeException {
        if (passengerList.isEmpty())
            this.addTicket(selectedDestination, selectedNoOfPassenger, selectedTicketType, Language.ENGLISH);

            return ticketService.generateTickets(passengerList);

    }


}
