package com.amay.tom.service.qrService2;

import com.amay.tom.agent.Agent;
import com.amay.tom.service.qrService2.implabs.OfflineQRTicketRefund;
import com.amay.tom.service.qrService2.implabs.PartialOnlineQRTicketRefund;
import com.amay.tom.service.qrService2.implabs.OnlineQRTicketRefund;

public class AbstractQRTicketRefunde extends AbstractQRTicketService {
    @Override
    public QRTicketService create(Agent agent) {
        return this.getService("1").create(agent);
    }

    private AbstractQRTicketRefunde getService(String type) {
        return switch (type) {
            case "1" -> new OnlineQRTicketRefund();
            case "2" -> new OfflineQRTicketRefund();
            case "3" -> new PartialOnlineQRTicketRefund();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

    }
}
