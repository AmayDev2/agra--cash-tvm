package com.amay.tom.model.siftdata;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
//@AllArgsConstructor
@ToString
public class TicketRecord {


    private int pid;

    private String ticketType;
    private int price;
    private String siftdate;
    private int count;
    private int sift;

    public TicketRecord(String ticketType, int price, String siftdate, int count, int sift) {
        this.ticketType = ticketType;
        this.price = price;
        this.siftdate = siftdate;
        this.count = count;
        this.sift = sift;
    }

}
