package com.amay.tom.service.validation.impl;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.config.TicketConfig;
import com.amay.tom.config.dto.TicketConfigDTO;
import com.amay.tom.enums.PassangerPossition;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.Station;
import com.amay.tom.model.analysis.ATicketAGStatusDTO;
import com.amay.tom.model.analysis.ATicketAnalysisDTO;
import com.amay.tom.model.tickets.QRTicketV2;
import com.amay.tom.repository.FareLine3;
import com.amay.tom.repository.StationData;
import com.amay.tom.service.validation.Validation;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tom.utils.faretable.FareCalculatorService;
import com.amay.tom.utils.time.TimeUtil;
import org.amaytechnosystems.TicketOperation;
import org.tinylog.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class QRValidation implements Validation {


    final int TICKET_ISSUE_TO_ENTRY_TIME_LIMIT_MIN;
    final int ENTRY_EXIT_PENALTY;
    final int sameStationOverStayPenaltyTime;
    final int overStayPenaltyTime;
    final int overstayPenaltyUnit;
    final int MAX_OVER_STAY_CHARGE;


    public QRValidation(){
        TICKET_ISSUE_TO_ENTRY_TIME_LIMIT_MIN=Math.toIntExact(Math.max( TicketConfig.INSTANT.getProductTypeDefDTO().getEntryAfterSale(),0));
        sameStationOverStayPenaltyTime= Math.toIntExact(Math.max( TicketConfig.INSTANT.getProductTypeDefDTO().getMaxStaySameStation(),0));
        ENTRY_EXIT_PENALTY= (int) Math.max(TicketConfig.INSTANT.getProductTypeDefDTO().getTailgatingCharges(),0);
        overStayPenaltyTime=Math.toIntExact(Math.max( TicketConfig.INSTANT.getProductTypeDefDTO().getMaxStayOtherStation(),0));
        overstayPenaltyUnit= Math.toIntExact(Math.max((int) TicketConfig.INSTANT.getProductTypeDefDTO().getOverstayCharges(),0));
        MAX_OVER_STAY_CHARGE=Math.toIntExact(Math.max((int) TicketConfig.INSTANT.getProductTypeDefDTO().getMaxOverstayCharges(),0));
    }



    @Override
    public boolean entryValidation(String ticketIssue) {
            return  TimeUtil.compareForAnalysis(ticketIssue, TICKET_ISSUE_TO_ENTRY_TIME_LIMIT_MIN);
    }


    @Override
    public boolean entryValidation(QRTicket qrTicket) {

        if (TICKET_ISSUE_TO_ENTRY_TIME_LIMIT_MIN == 0) {
        return TimeUtil.compareForAnalysis(qrTicket.getInitiateDateTime());
        }else {
            if (this.isUnderEntryTimeLimit(qrTicket.getInitiateDateTime(), TICKET_ISSUE_TO_ENTRY_TIME_LIMIT_MIN))
                return true;
            else
                Logger.warn("Ticket is expired");

            return false;
        }
    }

     @Override
     public int entryExitMismatch(PassangerPossition passangerPossition, int status) {  //status is just ticket taps(based on odd even)
        if(PassangerPossition.UNPAID.equals(passangerPossition)){
             return  status%2==0?0:ENTRY_EXIT_PENALTY;
         }else{
            return  status%2==0?ENTRY_EXIT_PENALTY:0;
        }
     }

     @Override
     public int getEntryExitPenalty(){
        return ENTRY_EXIT_PENALTY;
     }



     private boolean isUnderEntryTimeLimit(String initiateDateTime, int ticketIssueToEntryTimeLimitMin) {
        return TimeUtil.addedMinIsBeforeNow(initiateDateTime,ticketIssueToEntryTimeLimitMin);
    }

    @Override
    public int overStayValidation(QRTicketV2 qrTicket) {

        if(!this.isOverstay(qrTicket.getIssueAt(),qrTicket.getValidUntil()))
            return 0;

        return this.getPenalty(String.valueOf(qrTicket.getValidUntil()));

    }



    private int calculatePenalty(long validTimeMillis) {
        long now = System.currentTimeMillis(); // simpler and more accurate
        long overTimeMillis = now - validTimeMillis;
        Logger.info("Now: " + now + ", Valid Time: " + validTimeMillis);

        if (overTimeMillis <= 0) return 0;

        long overstayHours = (long) Math.ceil((double) overTimeMillis / (1000 * 60 * 60));
        return (int) (overstayPenaltyUnit * overstayHours);
    }

     @Override
     public int overStrayValidation(ATicketAnalysisDTO aTicketAnalysisDTO) {
         //TEST**************** TODO: Test
/*
         aTicketAnalysisDTO.getTicket().setSourceStation(SystemConfig.getInstance().getCurrentStation().getStationId());

         String time=aTicketAnalysisDTO.getTicket().getTicketIssue();
         ATicketAGStatusDTO aTicketAGStatusDTO1=new ATicketAGStatusDTO();
         aTicketAGStatusDTO1.setTime(time);
         aTicketAnalysisDTO.getAgStatus().add( aTicketAGStatusDTO1);
*/

         //Test****************

         int penaltyAmount=0;
         Station currentStation= SystemConfig.getInstance().getCurrentStation();
         Optional<ATicketAGStatusDTO> aTicketAGStatusDTO =
                 this.getLastEntry(aTicketAnalysisDTO.getAgStatus());
         if(aTicketAGStatusDTO.isEmpty()){
             Logger.info("Didn't Find anny Entry");
             return 0;
         }

         //same station  (TODO: match from ticket entry station not issue station)
         if(currentStation.equals(StationData.getInstance().getStation(aTicketAnalysisDTO.getTicket().getSourceStation().substring(2)))){
             penaltyAmount=calculatePenalty(aTicketAGStatusDTO.get().getTime().isBlank()
                     ?Long.parseLong(aTicketAnalysisDTO.getTicket().getTicketIssue()):Long.parseLong(aTicketAGStatusDTO.get().getTime())+(sameStationOverStayPenaltyTime*60*1000));
         }else{
//             long time=Long.parseLong(aTicketAGStatusDTO.get().getTime());
//             long plus=(overStayPenaltyTime*60*1000);
             penaltyAmount=calculatePenalty(Long.parseLong(aTicketAGStatusDTO.get().getTime())+(overStayPenaltyTime*60*1000));
         }
         return  Math.min(penaltyAmount,MAX_OVER_STAY_CHARGE);
     }

     private Optional<ATicketAGStatusDTO> getLastEntry(List<ATicketAGStatusDTO> entries) {
         ATicketAGStatusDTO aTicketAGStatusDTO=null;

         return entries.stream().filter(x->
                 TicketOperation.ENTRY.equals(x.getOperation())).max(Comparator.comparingLong(x-> Long.parseLong(x.getTime())));

     }

     private int getPenalty( String expiryTime) {
        return 20;
        //return (int) ((int)TimeUtil.getCurrentTimeInLong()-TimeUtil.getTimeInMilli(expiryTime));
    }


    private boolean isOverstay(long initiateDateTime, long exitDateTime) {
        return TimeUtil.isExpired(String.valueOf(exitDateTime));
    }

     @Override
     public int overTravelValidationFareBased(QRTicketV2 qrTicket,double fareMultiplayer) {

         if (qrTicket.getOutStation() == null ||
                 qrTicket.getOutStation().getStationName() == null ||
                 qrTicket.getOutStation().getStationName().trim().isEmpty()) {
             Logger.info("Invalid or missing destination station: {}", qrTicket.getOutStation());
             throw new RuntimeException("Invalid or missing destination station");
         }

         int source = Integer.parseInt(qrTicket.getInStation().getStationId());
         int destination = Integer.parseInt(qrTicket.getOutStation().getStationId());
         int current = Integer.parseInt(SystemConfig.getInstance().getCurrentStation().getStationId());

         //TODO: handel GT,RJT,SJT + Fare Multiplayer
//         int ticketFare = FareLine3.distanceMatrix[source - 1][destination - 1];
//         int actualFare = FareLine3.distanceMatrix[source - 1][current - 1]
         int ticketFare= qrTicket.getAmount();
         int actualFare = FareCalculatorService.getFare(qrTicket.getTicketType(),qrTicket.getTicketType().getFareMultiplayer(),source,current ,fareMultiplayer);

         return Math.max(0, actualFare - ticketFare);
     }

     @Override
     public int overTravelValidation(QRTicketV2 qrTicket) {
         if (qrTicket.getOutStation() == null ||
                 qrTicket.getOutStation().getStationName() == null ||
                 qrTicket.getOutStation().getStationName().trim().isEmpty()) {
             Logger.info("Invalid or missing destination station: {}", qrTicket.getOutStation());
             return 100; // Generic penalty for incomplete travel
         }

         int source = Integer.parseInt(qrTicket.getInStation().getStationId());
         int destination = Integer.parseInt(qrTicket.getOutStation().getStationId());
         int current = Integer.parseInt(SystemConfig.getInstance().getCurrentStation().getStationId());

         boolean isForward = destination > source;

         if (isForward) {
             // Valid travel direction: source → destination
             if (current > destination) {
                 // Over-travel forward
                 return FareLine3.distanceMatrix[destination - 1][current - 1];
             } else if (current < source) {
                 // Wrong direction
                 return FareLine3.distanceMatrix[current - 1][source - 1];
             }
         } else {
             // Valid travel direction: source ← destination
             if (current < destination) {
                 // Over-travel backward
                 return FareLine3.distanceMatrix[current - 1][destination - 1];
             } else if (current > source) {
                 // Wrong direction
                 return FareLine3.distanceMatrix[source - 1][current - 1];
             }
         }

         // Inside valid range, no penalty
         return 0;
     }



//    @Override
//    public int overTravelValidation(QRTicketV2 qrTicket) {
//        if(qrTicket.getOutStation()!=null && qrTicket.getOutStation().getStationName().trim().length()<0) {
//            Logger.info("To: {}", qrTicket.getOutStation());
//            return 100;
//        }
//
//        int source = Integer.parseInt(qrTicket.getInStation().getStationId());
//        int destination=Integer.parseInt(qrTicket.getOutStation().getStationId());
//        int current= Integer.parseInt(SystemConfig.getInstance().getCurrentStation().getStationId());
//        if(qrTicket.getOutStation()!=null && destination-source>=0) {
//            Logger.info("To: {}", qrTicket.getOutStation());
//            return 100;
//        }
//
//
////        if(source<=current || current<=destination)return 0;
//        else if(source>current){
//            return  FareLine3.distanceMatrix[current-1][source-1];
//        }else if(current>destination){
//            return  FareLine3.distanceMatrix[destination-1][current-1];
//        }else{
//            return 0;
//        }
//
////       return this.getOverTravelPenalty(qrTicket.getOutStation().getStationId());
//
//    }

    private int getOverTravelPenalty(String to) {
        return 100;
    }

    @Override
    public boolean freeTicketValidation(QRTicket qrTicket) {
        return false;
    }

    @Override
    public boolean exitValidation(QRTicketV2 qrTicket) {
       return this.overStayValidation(qrTicket)==0 && this.overTravelValidation(qrTicket)==0;

    }




     @Override
    public int getPenaltyAmount(QRTicketV2 qrTicket){
        return this.overStayValidation(qrTicket)+this.overTravelValidation(qrTicket);
    }


}
