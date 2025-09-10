package com.amay.tom.model;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.Equipment;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.Ticket;
import com.amay.tom.model.TicketType;
import com.amay.tom.model.station.Station;
import com.amay.tom.utils.time.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@ToString
public class MetroTicket {

    @Getter
    private String lineNumber;
    private Station stationId;

    private Equipment equipment;

    @Getter
    private long issuanceTime;
//    private int expireTime;

    @Getter
    private long validityTime;

    @Getter
    private int issueTime;
    private Station source;
    @Getter
    @Setter
    private Station destination;
    private com.amay.tom.model.Ticket ticket;


//    public MetroTicket(String lineNumber, long issuanceTime, int expireTime, Station source, Station destination, Ticket ticket) {
//        this.lineNumber = lineNumber;
//        this.issuanceTime = issuanceTime;
//        this.expireTime = expireTime;
//        this.source = source;
//        this.destination = destination;
//        this.ticket = ticket;
//        this.equipment=new Equipment("eq1","eqsr1");
//    }

//    public MetroTicket(String lineNumber, Station stationId, long issuanceTime, int expireTime, Station source,
//                       Station destination, String ticketId, int fare, TicketType ticketType, int ticketQuantity){
//        this.lineNumber=lineNumber;
//        this.stationId=stationId;
//        this.issuanceTime=issuanceTime;
//        this.expireTime=expireTime;
//        this.source=source;
//        this.destination=destination;
//        this.ticket=new Ticket(ticketId,fare,ticketType,ticketQuantity);
//        this.equipment= SystemConfig.getInstance().getCurrentEquipment();
//    }

    public MetroTicket(String lineNumber, Station stationId, long issuanceTime, long expireTime, Station source,
                       Station destination, String ticketId, int fare, com.amay.tom.model.TicketType ticketType, int ticketQuantity) {
        this.lineNumber = lineNumber;
        this.stationId = stationId;
        this.issuanceTime = issuanceTime;
        this.validityTime = expireTime;
        this.source = source;
        this.destination = destination;
        Logger.info(ticketQuantity);
        this.ticket = new Ticket(ticketId, fare, ticketType, ticketQuantity);
        this.equipment = SystemConfig.getInstance().getCurrentEquipment();

    }

    public String getIssueDay() {
        Date time = new Date(issuanceTime * 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        int year = calendar.get(Calendar.YEAR) % 100; // Get last two digits of the year
        int month = calendar.get(Calendar.MONTH) + 1; // Month starts from 0
        int day = calendar.get(Calendar.DAY_OF_MONTH) % 100; // Get last two digits of the day

        return String.format("%02d%02d%02d", month, year, day);
    }

    public String getIssueHM() {
        Date time = new Date(issuanceTime * 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        int hour = calendar.get(Calendar.HOUR) + (calendar.get(Calendar.AM_PM) == Calendar.PM ? 12 : 0); // Adjust hour for PM
        int minute = calendar.get(Calendar.MINUTE);

        // Extract last two digits of hour and minute
        String hourStr = String.format("%02d", hour);
        String minStr = String.format("%02d", minute);

        Logger.info("Time: {}", time);
        Logger.info("Hour: {}, Min: {}", hourStr, minStr);

        return hourStr + minStr;
    }


    public String getMetroNumber() {
        return lineNumber;
    }

    public String getStationId() {
        return this.stationId.getStationId();
    }

//    public Equipment getEquipment() {
//        return this.getEquipment();
//    }

    public String getEquipmentId() {
        return equipment.getEquipmentId();
    }

    public String getEquipmentSerial() {
        return equipment.getEquipmentSerial();
    }

//    public long getIssuanceTime() {
//        return issuanceTime;
//    }

//    public int getExpireTime() {
//        return expireTime;
//    }

    public String getSource() {
        return this.source.getStationId();
    }

    public String getDestinationName() {
        return this.destination.getStationName();
    }

    public String getSourceName() {
        return this.source.getStationName();
    }

    public String getDestination() {
        String dest = this.destination.getStationId();
//        //System.out.println(dest);
        return dest;
    }

//    public Ticket getTicket() {
//        return this.ticket;
//    }

    public String getTicketId() {
        return ticket.getTicketId();
    }

    public int getFare() {
        return ticket.getFare();
    }

    public TicketType getTicketType() {
        return ticket.getTicketType();
    }

    public int getTicketQuantity() {
        return ticket.getTicketQuantity();
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    public String toString() {

        return "Metro Number: " + lineNumber + "\n" +
                "Station Id: " + stationId.getStationId() + "\n" +
                "Equipment Id: " + equipment.getEquipmentId() + "\n" +
                "Equipment Serial: " + equipment.getEquipmentSerial() + "\n" +
                "Issuance Time: " + sdf.format(new Date(issuanceTime * 1000)) + "\n" +
                "Expire Time: " + sdf.format(new Date(this.getValidityTime() * 1000)) + "\n" +
                "Source: " + source.getStationId() + "\n" +
                "Destination: " + destination.getStationId() + "\n" +
                "Ticket Id: " + ticket.getTicketId() + "\n" +
                "Fare: " + ticket.getFare() + "\n" +
                "Ticket Type: " + ticket.getTicketType().getTicketTypeName() + " " + ticket.getTicketType().getTicketTypeId() + "\n" +
                "Ticket Quantity: " + ticket.getTicketQuantity() + "\n";
    }

    public com.amay.tom.model.QRTicket getQRTicket() {

        String sr = TimeUtil.epochToFormattedSystemTime(String.valueOf(validityTime), null);  //TODO: Check if this is correct

        Logger.info("Validity Time: {}", sr);
        return new com.amay.tom.model.QRTicket(this.getTicketId(),
                this.sdf.format(new Date(issuanceTime * 1000)),
                TimeUtil.epochToFormattedSystemTime(String.valueOf(validityTime), null),
                this.source.getStationName(),
                this.destination.getStationName(),
                this.getTicketType().getTicketTypeName(),
                "Card",
                String.valueOf(ticket.getFare()),
                null);
    }

    public com.amay.tom.model.QRTicket getAdjustedQRTicket() {

        String sr = TimeUtil.epochToFormattedSystemTime(String.valueOf(validityTime), null);  //TODO: Check if this is correct

        Logger.info("Validity Time: {}", sr);
        return new QRTicket(this.getTicketId(),
                this.sdf.format(new Date(issuanceTime * 1000)),
                TimeUtil.epochToFormattedSystemTime(String.valueOf(validityTime), null),
                this.source.getStationName(),
                this.destination.getStationName(),
                this.getTicketType().getTicketTypeName(),
                "Card",
                String.valueOf(ticket.getFare()),
                null);
    }


    public void setTicketId(String ticketId) {
        this.ticket.setTicketId(ticketId);
    }
}

