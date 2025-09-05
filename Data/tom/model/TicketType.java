package com.amay.tom.model;

public enum TicketType {
    SINGLE("01", "SJT", 60,1),
    RETURN("02", "RJT", 24*60,2),
    GROUP("03", "Group", 60,1),
    FREE("04", "Free", (15),0),
    PAID("05","Paid",15,1),
    TEST("06","Test",15,1);

    private final String ticketTypeId;
    private final String ticketTypeName;
    private final int ticketTime;
    private final int fareMultiplayer;

    TicketType(String ticketTypeId, String ticketTypeName, int ticketTime,int fareMultiplayer) {
        this.ticketTypeId = ticketTypeId;
        this.ticketTypeName = ticketTypeName;
        this.ticketTime = ticketTime;
        this.fareMultiplayer=fareMultiplayer;
    }

    public String getTicketTypeId() {
        return ticketTypeId;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public int getTicketTime(){return ticketTime;}

    public int getFareMultiplayer(){return fareMultiplayer;}

    public static TicketType getTicket(String id){
        return SINGLE.getTicketTypeId().equals(id)?
                SINGLE: RETURN.getTicketTypeId().equals(id)?
                RETURN:GROUP.getTicketTypeId().equals(id)?
                GROUP:FREE.getTicketTypeId().equals(id)?
                FREE:PAID.getTicketTypeId().equals(id)?PAID:null;
    }
}

