package com.amay.tom.model.tickets;

import com.amay.tom.model.TicketType;
import lombok.Data;
import lombok.experimental.Accessors;

import javafx.scene.image.Image;

@Accessors(chain = true)
@Data
public class UIQRTicket {
    private Image qrCode;
    private String ticketId;
    private TicketType ticketType;
    private String source;
    private String destination;
    private String quantity;
    private String price;
    private String issuedAt;
    private String validUntil;
}