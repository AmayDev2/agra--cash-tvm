package com.amay.tom.service.qrService2;

import com.amay.tom.enums.AdjustmentType;
import com.amay.tom.model.station.Station;
import com.amay.tom.model.tickets.PreGeneratadTicket;
import com.amay.tom.model.tickets.QRTicketV2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@NoArgsConstructor
@Data
public class TicketInfo  {
    private Station destination;
    private Station source;
    private long effectiveTime;
    private long exitExtendedDurationInMin;
    private long time;
    private HashSet<AdjustmentType> adjustmentType;
    private int pAmount;
    private String qrData; // old data
    PreGeneratadTicket preGeneratadTicket;
    QRTicketV2 qrTicketV2;
    private String area;
    public TicketInfo(QRTicketV2 qrTicketV2) {
        this.qrTicketV2=qrTicketV2;

    }


}
