package com.amay.tom.grpc.monotoring;

import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.Alarm;
import com.amay.tom.enums.ConnectionStatus;
import com.amay.tom.service.events.commands.DeviceInfoCommand;
import com.amay.tom.service.events.commands.PheStatusCommand;
import com.amay.tom.service.tom.RemoteListener;
import org.network.monitorandcontrol.Alarms;
import org.network.monitorandcontrol.OperationMode;
import org.network.monitorandcontrol.SpecialMode;
import org.tinylog.Logger;

import javax.management.ServiceNotFoundException;
import java.util.Arrays;


// CCU Monitoring Listener for GRPC, the class is responsible to listen to the commands and alarms and send it to server using GRPC,
// the class implements RemoteListener interface  so that it can implement the all RemoteListener methods
public class GrpcApiListener implements RemoteListener {

    private Agent agent;
    private final GrpcControlMonitoringService grpcControlMonitoringService;
    public GrpcApiListener(GrpcControlMonitoringService grpcControlMonitoringService, Agent agent) {
        // TODO implement here
        this.grpcControlMonitoringService = grpcControlMonitoringService;
        this.agent = agent;
    }

    public ConnectionStatus getConnectionStatus() {
        return grpcControlMonitoringService.getConnectionStatus();
    }


//    public void setApiInstance(GrpcControlMonitoringService grpcControlMonitoringService) {
//        // TODO implement here
//        this.grpcControlMonitoringService = grpcControlMonitoringService;
//    }

    public void sendDeviceInfo() {
        Logger.info("Got command to,Sending device info to server");
        org.network.monitorandcontrol.tom.TOMEquipmentInfo builder = org.network.monitorandcontrol.tom.TOMEquipmentInfo.newBuilder()
                .setEquipId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(org.network.monitorandcontrol.EquipmentType.TOM.name())
                .setTomIp(SystemConfig.getInstance().getTomIp())
                .setEquipName(SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial())
                .build();
        grpcControlMonitoringService.sendMessage(RequestHandler.setDeviceInfo(builder));
    }

    public void sendPeripheralStatus(int[] deviceStatus) {
        if(null==deviceStatus){
            Logger.warn("Sending peripheral status is NULL");
            return;
        }
        Logger.info("Sending peripheral status to server: "+ Arrays.toString(deviceStatus));
        org.network.monitorandcontrol.tom.TOMPeripheralStatus builder = org.network.monitorandcontrol.tom.TOMPeripheralStatus.newBuilder()
                        .setPrinterConnected(deviceStatus[1] == 1)
                                .setScannerConnected(deviceStatus[0] == 1)
                                        .setCcuConnected(deviceStatus[2] == 1)
                                                .build();
        grpcControlMonitoringService.sendMessage(RequestHandler.setPeripheralStatus(builder));

    }


    public void sendOperationMode(OperationMode operationMode) {

        Logger.info("Sending operation mode to server: "+ operationMode);
        org.network.monitorandcontrol.tom.TOMModeControl builder = org.network.monitorandcontrol.tom.TOMModeControl.newBuilder()
                .setOperationMode(operationMode)
                .build();
        grpcControlMonitoringService.sendMessage(RequestHandler.setOperationMode(builder));

    }
    public void sendSpecialMode(SpecialMode operationMode) {

        Logger.info("Sending operation mode to server: "+ operationMode);
        org.network.monitorandcontrol.tom.TOMModeControl builder = org.network.monitorandcontrol.tom.TOMModeControl.newBuilder()
                .setSpecialMode(operationMode)
                .build();
        grpcControlMonitoringService.sendMessage(RequestHandler.setOperationMode(builder));

    }



    //adding one more function to send Alarms to server

    public boolean sendAlarm(Alarm alarmMessage) {
        Logger.info("Sending alarm to server: "+ alarmMessage);
        Alarms.Builder alarms = Alarms.newBuilder();
//        alarm.forEach(alarmMessage -> {
            alarms.putAlarms(alarmMessage.getCode(), alarmMessage.getMessage());
//        });
        grpcControlMonitoringService.sendMessage(RequestHandler.setAlarm(alarms.build()));
        return true;
    }

    @Override
    public void sunleBhai(Class<?> commandClass) throws ServiceNotFoundException {
        Logger.info("sunle Bhai called in GrpcApiListener");
        if (commandClass.equals(DeviceInfoCommand.class)) {
            this.sendDeviceInfo();
        } else if (commandClass.equals(PheStatusCommand.class)) {
            this.sendPeripheralStatus(agent.getDeviceStatusListener().getDeviceStatus());
        } else {
            Logger.error("Command not found in GrpcApiListener");
        }
        this.sendDeviceInfo();
    }

    public void sendInServiceOperationMode(OperationMode operationMode, boolean qr, boolean card) {
        Logger.info("Sending operation mode to server: "+ operationMode);
        org.network.monitorandcontrol.tom.TOMModeControl builder = org.network.monitorandcontrol.tom.TOMModeControl.newBuilder()
                .setOperationMode(operationMode)
                .setQrSaleMode(qr)
                .setCardProcessMode(card)
                .build();
        grpcControlMonitoringService.sendMessage(RequestHandler.setOperationMode(builder));
    }
}
