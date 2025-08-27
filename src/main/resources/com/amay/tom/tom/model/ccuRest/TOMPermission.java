package com.amay.tom.model.ccuRest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TOMPermission implements Serializable {

    private long id;

    private boolean qrTicketSale = false;
    private boolean qrTicketAnalysis = false;
    private boolean qrTicketAdjustment = false;
    private boolean qrTicketCancellation = false;
    private boolean qrTicketRefund = false;
    private boolean qrTicketReprint = false;
    private boolean qrTicketReplacement = false;
    private boolean qrFreeTicket = false;
    private boolean qrPaidTicket = false;
    private boolean tvm = false;
    private boolean ncmcSale = false;
    private boolean ncmcAnalysis = false;
    private boolean importAndExport= false;
    private boolean shutdownAndRestart = false;

    
    // Getters and setters...
    
    
}
