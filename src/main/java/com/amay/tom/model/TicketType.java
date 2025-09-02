package com.amay.tom.model;

import com.amay.tom.model.product.Product;

public enum TicketType {
    SINGLE("01", "SJT", 60,1,true,new Product()),
    RETURN("02", "RJT", 24*60,2,true,new Product()),
    GROUP("03", "Group Ticket", 60,1,true,new Product()),
    FREE("04", "Free Ticket", (15),0,false,new Product()),
    PAID("05","Paid Ticket",15,1,false,new Product()),
    MQR_SINGLE("06","SJT",15,1,true,new Product()),
    MQR_RETURN("07","RJT",15,1,true,new Product()),
    MQR_GROUP("08","Group Ticket",15,1,true,new Product());


    private final String ticketTypeId;
    private final String ticketTypeName;
    private final int ticketTime;
    private final int fareMultiplayer;
    private final boolean refundable;
    private  Product product;

    TicketType(String ticketTypeId, String ticketTypeName, int ticketTime,int fareMultiplayer, boolean refundable, Product product) {
        this.product = product;
        this.refundable = refundable;
        this.ticketTypeId = ticketTypeId;
        this.ticketTypeName = ticketTypeName;
        this.ticketTime = ticketTime;
        this.fareMultiplayer=fareMultiplayer;
    }
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getTicketTypeId() {
        return ticketTypeId;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public int getTicketTime(){return ticketTime;}

    public int getFareMultiplayer(){return fareMultiplayer;}
    public boolean isRefundable() {
        return refundable;
    }

    public static TicketType getTicket(String id){
        TicketType ticketType= SINGLE.getTicketTypeId().equals(id)?
                SINGLE: RETURN.getTicketTypeId().equals(id)?
                RETURN:GROUP.getTicketTypeId().equals(id)?
                GROUP:FREE.getTicketTypeId().equals(id)?
                FREE:PAID.getTicketTypeId().equals(id)?PAID:MQR_SINGLE.getTicketTypeId().equals(id) ? MQR_SINGLE :
                MQR_RETURN.getTicketTypeId().equals(id) ? MQR_RETURN :
                        MQR_GROUP.getTicketTypeId().equals(id) ? MQR_GROUP :null;
        if(ticketType==null) {
            throw new IllegalArgumentException("Invalid Ticket Type ID: " + id);
        }
        if(!ticketType.getProduct().isActive()) {
            throw new IllegalArgumentException("Product not Active: " + id);
        }
        return ticketType;

    }
}

