package com.amay.tom.grpc.monotoring;

import com.amay.tom.config.SystemConfig;
import com.google.protobuf.Any;
import org.network.monitorandcontrol.Alarms;
import org.network.monitorandcontrol.EquipmentType;
import org.network.monitorandcontrol.OperationMode;
import org.network.monitorandcontrol.RequestType;
import org.network.monitorandcontrol.tom.TOMEquipmentInfo;
import org.network.monitorandcontrol.tom.TOMModeControl;
import org.network.monitorandcontrol.tom.TOMPeripheralStatus;
import org.network.monitorandcontrol.tom.TOMProtocol;
import org.tinylog.Logger;


public class RequestHandler {

    static String IP,ID,NAME,SHIFT;

    public static org.network.monitorandcontrol.tom.TOMProtocol getInitialRequest() {

        TOMProtocol msj = TOMProtocol.newBuilder().setRequestType(org.network.monitorandcontrol.RequestType.DEVICE_INFO)
                .setRequestData(Any.pack(TOMEquipmentInfo.newBuilder()
                        .setEquipId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                        .setEquipmentType(EquipmentType.TOM.name())
                        .setTomIp("192.168.1.3")
                        .setEquipName("TOM_GNDA_001")
                        .build()))
                .build();
        return msj;
    }


    public static TOMProtocol setPeripheralStatus(TOMPeripheralStatus peripheralStatus) {
        Logger.debug("Sending peripheral status to server: "+ peripheralStatus);
        return TOMProtocol.newBuilder().setRequestType(org.network.monitorandcontrol.RequestType.PERIPHERAL_STATUS)
                .setRequestData(Any.pack(peripheralStatus))
                .build();
    }


    public static TOMProtocol setDeviceInfo(TOMEquipmentInfo deviceInfo) {
        return TOMProtocol.newBuilder().setRequestType(org.network.monitorandcontrol.RequestType.DEVICE_INFO)
                .setRequestData(Any.pack(deviceInfo))
                .build();
    }

    public static TOMProtocol setParameterVersion(org.network.monitorandcontrol.tom.TOMParameterVersion parameterVersion) {
        return TOMProtocol.newBuilder().setRequestType(org.network.monitorandcontrol.RequestType.PARAMETER_VERSION)
                .setRequestData(Any.pack(parameterVersion))
                .build();
    }

    public static TOMProtocol setOperationMode(TOMModeControl tomModeControl) {
        return TOMProtocol.newBuilder().setRequestType(RequestType.RESPONSE)
                .setRequestData(Any.pack(tomModeControl))
                .build();
    }

    public static TOMProtocol testSetOperationModeMaintenance() {
        return TOMProtocol.newBuilder().setRequestType(RequestType.RESPONSE)
                .setRequestData(Any.pack(TOMModeControl.newBuilder().setOperationMode(OperationMode.MAINTENANCE).build()))
                .build();
    }

    public static TOMProtocol testSetOperationModeTest() {
        return TOMProtocol.newBuilder().setRequestType(RequestType.RESPONSE)
                .setRequestData(Any.pack(TOMModeControl.newBuilder().setOperationMode(OperationMode.MAINTENANCE).build()))
                .build();
    }


    public static TOMProtocol setAlarm(Alarms build) {
        try {
            return TOMProtocol.newBuilder().setRequestType(RequestType.ALARMS)
                    .setRequestData(Any.pack(build))
                    .build();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
