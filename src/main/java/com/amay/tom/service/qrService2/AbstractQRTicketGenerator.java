package com.amay.tom.service.qrService2;

import com.amay.tom.agent.Agent;
import com.amay.tom.service.qrService2.implabs.*;

public class AbstractQRTicketGenerator extends AbstractQRTicketService {


    @Override
    public QRTicketService create(Agent agent) {

        return this.getService(agent.getPeripheralMonitor().isCcu_connected()?1:agent.getPeripheralMonitor().isScu_connected()?2:0).create(agent);
    }

    private AbstractQRTicketGenerator getService( int isCCUConnected) {
        return switch (isCCUConnected) {
            case 1 -> new OnlineQRTicketGenerator();
            case 0 -> new OfflineQRTicketGenerator();
            case 2 -> new PartialOnlineQRTicketGenerator();
            default -> throw new IllegalStateException("Unexpected value: " + isCCUConnected);
        };

    }
}
