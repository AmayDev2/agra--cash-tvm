package com.amay.tom.service.qrService2.impl;

import com.amay.tom.agent.Agent;
import com.amay.tom.service.qrService2.AbstractQRTicketRefunde;
import com.amay.tom.service.qrService2.RefundQRService;
import com.amay.tom.service.qrService2.impl.RefundQRServiceImpl;

public class PartialOnlineQRTicketRefund extends RefundQRServiceImpl {

    public  PartialOnlineQRTicketRefund(Agent agent){
        super(agent);
    }

}