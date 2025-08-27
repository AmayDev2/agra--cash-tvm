package com.amay.tom.service.controller;

import com.amay.tom.enums.Language;
import com.amay.tom.exceptions.NotFoundStationException;
import com.amay.tom.exceptions.TicketNotAddedException;
import com.amay.tom.exceptions.TicketNotFoundException;
import com.amay.tom.model.MetroTicket;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.station.Station;

import java.util.ArrayList;

public interface MainUIService {
    Station[] getStationDetails() throws NotFoundStationException;

    TicketType[] getTicketTypeDetails()throws TicketNotFoundException;

    void addTicket(Station selectedDestination, int selectedNoOfPassenger, TicketType selectedTicketType, Language language) throws TicketNotAddedException;

    ArrayList<MetroTicket> ConfirmTicket(Station selectedDestination, int selectedNoOfPassenger, TicketType selectedTicketType) throws RuntimeException;
}
