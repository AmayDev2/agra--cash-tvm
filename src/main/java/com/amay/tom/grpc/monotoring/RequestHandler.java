package com.amay.tom.grpc.monotoring;

import com.amay.tom.config.SystemConfig;
import com.google.protobuf.Any;
import org.network.monitorandcontrol.Alarms;
import org.network.monitorandcontrol.EquipmentType;
import org.network.monitorandcontrol.OperationMode;
import org.network.monitorandcontrol.RequestType;
import org.network.monitorandcontrol.tom.TOMEquipmentInfo;
import org.network.monitorandcontrol.tom.TOMParameterVersion;
import org.network.monitorandcontrol.tom.TOMPeripheralStatus;
import org.network.monitorandcontrol.tom.TOMModeControl;
import org.network.monitorandcontrol.tvm.*;
import org.tinylog.Logger;


public class RequestHandler {

    static String IP,ID,NAME,SHIFT;

    public static TVMProtocol getInitialRequest() {

        TVMProtocol msj = TVMProtocol.newBuilder().setRequestType(RequestType.DEVICE_INFO)
                .setRequestData(Any.pack(TVMEquipmentInfo.newBuilder()
                        .setEquipId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                        .setEquipmentType(EquipmentType.TVM.name())
                        .setEquipIp("192.168.1.3")
                        .setEquipName("TOM_GNDA_001")
                        .build()))
                .build();
        return msj;
    }


    public static TVMProtocol setPeripheralStatus(TVMPeripheralStatus peripheralStatus) {
        Logger.debug("Sending peripheral status to server: "+ peripheralStatus);
        return TVMProtocol.newBuilder().setRequestType(RequestType.PERIPHERAL_STATUS)
                .setRequestData(Any.pack(peripheralStatus))
                .build();
    }


    public static TVMProtocol setDeviceInfo(TVMEquipmentInfo deviceInfo) {
        return TVMProtocol.newBuilder().setRequestType(RequestType.DEVICE_INFO)
                .setRequestData(Any.pack(deviceInfo))
                .build();
    }

    public static TVMProtocol setParameterVersion(TVMParameterVersion parameterVersion) {
        return TVMProtocol.newBuilder().setRequestType(RequestType.PARAMETER_VERSION)
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
                .setRequestData(Any.pack(TOMModeControl.newBuilder().setOperationMode(OperationMode.MAINTENANCE).build()))
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
