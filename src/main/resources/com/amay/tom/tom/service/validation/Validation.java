package com.amay.tom.service.validation;

import com.amay.tom.enums.PassangerPossition;
import com.amay.tom.model.QRTicket;
import com.amay.tom.model.analysis.ATicketAnalysisDTO;
import com.amay.tom.model.tickets.QRTicketV2;

public interface Validation {

    boolean dateValidation(String ticketIssue);

    boolean entryValidation(QRTicket qrTicket);


    int entryExitMismatch(PassangerPossition passangerPossition, int status);


    int getEntryExitPenalty();

    int overStayValidation(QRTicketV2 qrTicket);
    int overStrayValidation(ATicketAnalysisDTO aTicketAnalysisDTO);

    int overTravelValidation(QRTicketV2 qrTicket);

    int overTravelValidationFareBased(QRTicketV2 qrTicket,double fareMultiplayer);


    boolean freeTicketValidation(QRTicket qrTicket);

    boolean exitValidation(QRTicketV2 qrTicket);

    int getPenaltyAmount(QRTicketV2 expiryTime);

    boolean entryValidation(String ticketIssue,int time);
}
