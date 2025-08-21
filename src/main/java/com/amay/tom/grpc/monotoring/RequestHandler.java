package com.amay.tom.grpc.monotoring;

import com.amay.tom.config.SystemConfig;
import com.google.protobuf.Any;
import org.network.monitorandcontrol.Alarms;
import org.network.monitorandcontrol.DeviceType;
import org.network.monitorandcontrol.OperationMode;
import org.network.monitorandcontrol.RequestType;
import org.network.monitorandcontrol.tvm.TVMDeviceInfo;
import org.network.monitorandcontrol.tvm.TVMModeControl;
import org.network.monitorandcontrol.tvm.TVMPeripheralStatus;
import org.network.monitorandcontrol.tvm.TVMProtocol;
import org.tinylog.Logger;


public class RequestHandler {

    static String IP,ID,NAME,SHIFT;

    public static org.network.monitorandcontrol.tvm.TVMProtocol getInitialRequest() {

        TVMProtocol msj = TVMProtocol.newBuilder().setRequestType(org.network.monitorandcontrol.RequestType.DEVICE_INFO)
                .setRequestData(Any.pack(TVMDeviceInfo.newBuilder()
                        .setEquipId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                        .setDeviceType(DeviceType.TVM.name())
                                        
//                        .setTomIp("192.168.1.3")
                        .setEquipName("TOM_GNDA_001")
                        .build()))
                .build();
        return msj;
    }


    public static TVMProtocol setPeripheralStatus(TVMPeripheralStatus peripheralStatus) {
        Logger.debug("Sending peripheral status to server: "+ peripheralStatus);
        return TVMProtocol.newBuilder().setRequestType(org.network.monitorandcontrol.RequestType.PERIPHERAL_STATUS)
                .setRequestData(Any.pack(peripheralStatus))
                .build();
    }


    public static TVMProtocol setDeviceInfo(TVMDeviceInfo deviceInfo) {
        return TVMProtocol.newBuilder().setRequestType(org.network.monitorandcontrol.RequestType.DEVICE_INFO)
                .setRequestData(Any.pack(deviceInfo))
                .build();
    }

    public static TVMProtocol setParameterVersion(org.network.monitorandcontrol.tom.TOMParameterVersion parameterVersion) {
        return TVMProtocol.newBuilder().setRequestType(org.network.monitorandcontrol.RequestType.PARAMETER_VERSION)
                .setRequestData(Any.pack(parameterVersion))
                .build();
    }

    public static TVMProtocol setOperationMode(TVMModeControl tomModeControl) {
        return TVMProtocol.newBuilder().setRequestType(RequestType.RESPONSE)
                .setRequestData(Any.pack(tomModeControl))
                .build();
    }

    public static TVMProtocol testSetOperationModeMaintenance() {
        return TVMProtocol.newBuilder().setRequestType(RequestType.RESPONSE)
                .setRequestData(Any.pack(TVMModeControl.newBuilder().setOperationMode(OperationMode.MAINTENANCE).build()))
                .build();
    }

    public static TVMProtocol testSetOperationModeTest() {
        return TVMProtocol.newBuilder().setRequestType(RequestType.RESPONSE)
                .setRequestData(Any.pack(TVMModeControl.newBuilder().setOperationMode(OperationMode.MAINTENANCE).build()))
                .build();
    }


    public static TVMProtocol setAlarm(Alarms build) {
        try {
            return TVMProtocol.newBuilder().setRequestType(RequestType.ALARMS)
                    .setRequestData(Any.pack(build))
                    .build();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
