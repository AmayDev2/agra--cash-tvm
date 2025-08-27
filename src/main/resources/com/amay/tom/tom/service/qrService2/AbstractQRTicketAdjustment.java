package com.amay.tom.service.qrService2;

import com.amay.tom.agent.Agent;
import com.amay.tom.service.qrService2.implabs.OfflineQRTicketAdjust;
import com.amay.tom.service.qrService2.implabs.OnlineQRTicketAdjust;
import com.amay.tom.service.qrService2.implabs.PartialOnlineQRTicketAdjust;

public class AbstractQRTicketAdjustment extends AbstractQRTicketService {
    @Override
    public QRTicketService create(Agent agent) {
        return this.getService("2").create(agent);
    }

    private AbstractQRTicketAdjustment getService(String type) {
        return switch (type) {
            case "1" -> new OnlineQRTicketAdjust();
            case "2" -> new OfflineQRTicketAdjust();
            case "3" -> new PartialOnlineQRTicketAdjust();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

    }
}
