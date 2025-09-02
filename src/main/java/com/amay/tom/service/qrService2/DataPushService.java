package com.amay.tom.service.qrService2;

import com.amay.tom.service.qrService2.push.PushService;

public class DataPushService{
    private final PushService pushService;


    public DataPushService(PushService pushService) {
        this.pushService=pushService;
    }


    public void pushData() {
        this.pushService.push();

    }
}
