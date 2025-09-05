package com.amay.tom.service.fare;

import com.amay.tom.model.Station;
import com.amay.tom.model.TicketType;
import com.amay.tom.repository.FareLine3;

public class Fare {

    public static int getFare(Station sourceStation, Station destinationStation){
        int fare= FareLine3.distanceMatrix[Integer.parseInt(sourceStation.getStationId().substring(2))-1][Integer.parseInt(destinationStation.getStationId().substring(2))-1];
        System.out.println("Fare: "+fare);
        return fare;
    }



    public static int getTotalFare(Station sourceStation, Station destinationStation, int noOfPassenger){
        int fare= FareLine3.distanceMatrix[Integer.parseInt(sourceStation.getStationId().substring(2))-1][Integer.parseInt(destinationStation.getStationId().substring(2))-1];
        System.out.println("Fare: "+fare*noOfPassenger);
        return fare*noOfPassenger;
    }

    public static int getTotalFare(Station sourceStation, Station destinationStation, int noOfPassenger, TicketType ticketType){
        if(ticketType.equals(TicketType.FREE)){
            return 0;
        }
        int fare= FareLine3.distanceMatrix[Integer.parseInt(sourceStation.getStationId().substring(2))-1][Integer.parseInt(destinationStation.getStationId().substring(2))-1]*noOfPassenger;
        System.out.println("Fare: "+fare);
        if(ticketType.equals(TicketType.RETURN)){
            fare= fare*2;
        }
        return fare;
    }


}
