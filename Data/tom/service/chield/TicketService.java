package com.amay.tom.service.chield;

import com.amay.tom.model.*;

import java.util.ArrayList;


@Deprecated
public interface TicketService {
    MetroTicket generateTicket(Station source , Station Destination, Station stationId, int noOfPassenger, TicketType selectedTicketType);
    QRTicket[] generateQRTicket(MetroTicket[] metroTickets, String orderId);
    boolean printAndSaveTicket(QRTicket qrTicket);

    ArrayList<MetroTicket> generateTickets(ArrayList<Passenger> passengerList);
}



