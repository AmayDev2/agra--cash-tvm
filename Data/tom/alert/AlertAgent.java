package com.amay.tom.alert;

import com.amay.tom.enums.Alarm;
import com.amay.tom.grpc.monotoring.GrpcApiListener;

public enum AlertAgent implements SCUAlert  {
    INSTANCE;

    private GrpcApiListener grpcApiListener;

    AlertAgent() {

    }

    AlertAgent(GrpcApiListener grpcApiListener) {
        this.grpcApiListener = grpcApiListener;

    }


    @Override
    public void getAlert(Alarm alarm) {
        // TODO Auto-generated method stub
        this.grpcApiListener.sendAlarm(alarm);

    }
}
