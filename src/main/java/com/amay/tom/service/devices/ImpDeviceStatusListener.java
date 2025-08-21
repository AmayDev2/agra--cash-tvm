package com.amay.tom.service.devices;


import com.amay.tom.grpc.monotoring.GrpcApiListener;
import lombok.Getter;

public class ImpDeviceStatusListener implements DeviceStatusListener{

    @Getter
    private int[] deviceStatus ;
    private final GrpcApiListener grpcApiListener;

    public ImpDeviceStatusListener(GrpcApiListener grpcApiListener){
        this.grpcApiListener = grpcApiListener;

    }

    @Override
    public void onDeviceStatusChanged(int[] deviceStatus) {

        int index = 0;
//        Logger.debug("device status {} : {}", Helper.ObjectToJson(deviceStatus), Helper.ObjectToJson(this.deviceStatus));
        for(index=0; null!=this.deviceStatus && index < deviceStatus.length; index++){
            if(this.deviceStatus[index] != deviceStatus[index])
                break;
        }

        if(null==this.deviceStatus || index != deviceStatus.length){
            this.deviceStatus= deviceStatus;
            this.grpcApiListener.sendPeripheralStatus(deviceStatus);
//            Logger.info("Device status changed to: "+ Helper.ObjectToJson(deviceStatus));
        }

    }



}
