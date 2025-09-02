package com.amay.tom.grpc.monotoring;

import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.enums.Alarm;
import com.amay.tom.enums.ConnectionStatus;
import com.amay.tom.model.version.MasterConfigInfo;
import com.amay.tom.service.events.commands.DeviceInfoCommand;
import com.amay.tom.service.events.commands.PheStatusCommand;
import com.amay.tom.service.events.commands.VersionCommand;
import com.amay.tom.service.tom.RemoteListener;
import lombok.extern.slf4j.Slf4j;
import org.amaytechnosystems.AVersionInfo;
import org.network.monitorandcontrol.Alarms;
import org.network.monitorandcontrol.EquipmentType;
import org.network.monitorandcontrol.OperationMode;
import org.network.monitorandcontrol.SpecialMode;
import org.network.monitorandcontrol.tvm.TVMEquipmentInfo;
import org.network.monitorandcontrol.tvm.TVMModeControl;
import org.network.monitorandcontrol.tvm.TVMParameterVersion;
import org.network.monitorandcontrol.tvm.TVMPeripheralStatus;
import org.tinylog.Logger;

import javax.management.ServiceNotFoundException;
import java.util.Arrays;
import java.util.List;


// CCU Monitoring Listener for GRPC, the class is responsible to listen to the commands and alarms and send it to server using GRPC,
// the class implements RemoteListener interface  so that it can implement the all RemoteListener methods
@Slf4j
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
        TVMEquipmentInfo builder = TVMEquipmentInfo.newBuilder()
                .setEquipId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                .setEquipmentType(EquipmentType.TVM.name())
                .setEquipId(SystemConfig.getInstance().getTomIp())
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
        TVMPeripheralStatus builder = TVMPeripheralStatus.newBuilder()
                        .setPrinterConnected(deviceStatus[1] == 1)
                                .setScannerConnected(deviceStatus[0] == 1)
                                        .setCcuConnected(deviceStatus[2] == 1)
                                                .build();
        grpcControlMonitoringService.sendMessage(RequestHandler.setPeripheralStatus(builder));

    }


    public void sendOperationMode(OperationMode operationMode) {

        Logger.info("Sending operation mode to server: "+ operationMode);
        TVMModeControl builder = TVMModeControl.newBuilder()
                .setOperationMode(operationMode)
                .build();
        grpcControlMonitoringService.sendMessage(RequestHandler.setOperationMode(builder));

    }
    public void sendSpecialMode(SpecialMode operationMode) {

        Logger.info("Sending operation mode to server: "+ operationMode);
        TVMModeControl builder =TVMModeControl.newBuilder()
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
        } else if (commandClass.equals(VersionCommand.class)) {
            this.sendVersionInfo(agent.getMasterConfigInfo());
        } else {
            Logger.error("Command not found in GrpcApiListener");
        }
        this.sendDeviceInfo();
    }

    public void sendInServiceOperationMode(OperationMode operationMode, boolean qr, boolean card) {
        Logger.info("Sending operation mode to server: "+ operationMode);
        TVMModeControl builder = TVMModeControl.newBuilder()
                .setOperationMode(operationMode)
                .setQrSaleMode(qr)
                .setCardProcessMode(card)
                .build();
        grpcControlMonitoringService.sendMessage(RequestHandler.setOperationMode(builder));
    }

    public void sendVersionInfo(MasterConfigInfo masterConfigInfo){
        Logger.info("Sending version info to server: "+ masterConfigInfo);
        AVersionInfo aVersionInfo = AVersionInfo.newBuilder()
                .setEquipVer(masterConfigInfo.getConfigVer())
                .setSwVer(masterConfigInfo.getTomSwVer())
                .setFareVer(masterConfigInfo.getFareConfig())
                .setProductVer(masterConfigInfo.getProductConfig())
                .setBusinessDayVer(masterConfigInfo.getBusinessDayVer())
                .setPeakTimeVer(masterConfigInfo.getPeakTimeVer())
                .setCalendarVer(masterConfigInfo.getCalenderConfig())
                .setTopologyVer(masterConfigInfo.getTopologyConfig())
                .setUsersVer(masterConfigInfo.getUserVer())
                .setProfileVer(masterConfigInfo.getProfileVer())
                .build();

        TVMParameterVersion parameterVersion = TVMParameterVersion.newBuilder()
                .clearReaderInfo()
                .setVersionInfo(aVersionInfo).build();

        log.info("GRPC Channel name for PARAMETER_VERSION send via monitoring : {}", grpcControlMonitoringService.getChanelName());

        grpcControlMonitoringService.sendMessage(RequestHandler.setParameterVersion(parameterVersion));
    }
}
