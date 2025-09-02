package com.amay.tom.service.chield;

import com.amay.tom.model.*;
import com.amay.tom.model.station.Station;

import java.awt.image.BufferedImage;
import java.util.ArrayList;


@Deprecated
public interface TicketService {
    MetroTicket generateTicket(Station source , Station Destination, Station stationId, int noOfPassenger, TicketType selectedTicketType);
    QRTicket[] generateQRTicket(MetroTicket[] metroTickets, String orderId);
    BufferedImage getImage(QRTicket qrTicket);
    boolean printAndSaveTicket(QRTicket qrTicket);
    ArrayList<MetroTicket> generateTickets(ArrayList<Passenger> passengerList);
}



