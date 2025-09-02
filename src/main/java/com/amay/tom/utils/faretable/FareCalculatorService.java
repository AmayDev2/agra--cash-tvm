package com.amay.tom.utils.faretable;

import com.amay.tom.exceptions.NotValidTicketToCalculateFare;
import com.amay.tom.model.TicketType;
import com.amay.tom.repository.FareLine3;
import org.tinylog.Logger;

public class FareCalculatorService {

    public static int getFare(TicketType ticketType, int fareMultiplayer, int source, int destination, double  timeFareMultiplayer) {
        int FARE_MULTIPLAYER=fareMultiplayer;

        Logger.info("Fare Multiplayer {} source {} destination {}",FARE_MULTIPLAYER,source,destination);
        final int ticketPrice= switch (ticketType) {
            case SINGLE,RETURN,GROUP,FREE ->
                    (int)(timeFareMultiplayer
                            * FareLine3.distanceMatrix[ source - 1] [destination - 1]);
            case PAID ->FareLine3.distanceMatrix[0][FareLine3.distanceMatrix.length - 1]; // TODO: FATEMULTIPLAYER IS NOT appliwd
            default -> throw new NotValidTicketToCalculateFare("Not a valid ticket type");
        };
        return ticketPrice * FARE_MULTIPLAYER;
    }

}
