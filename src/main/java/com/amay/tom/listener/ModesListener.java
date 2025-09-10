package com.amay.tom.listener;

import com.amay.tom.agent.Agent;
import com.amay.tom.enums.Alarm;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.enums.DeviceStatus;
import com.amay.tom.model.equipment.EquipmentMapper;
import com.amay.tom.model.equipment.entity.EquipmentPrivilege;
import com.amay.tom.service.events.commands.*;
import com.amay.tom.service.tom.RemoteListener;
import org.network.monitorandcontrol.OperationMode;
import org.network.monitorandcontrol.SpecialMode;
import org.tinylog.Logger;

import javax.management.ServiceNotFoundException;

public class ModesListener implements RemoteListener {

    private final EquipmentPrivilege equipmentPrivilege;
    private final Agent agent;
    public ModesListener(Agent agent) {
        //System.out.println("ModesListener");
        this.equipmentPrivilege = agent.getEquipmentPrivilege();
        this.agent = agent;

    }

    @Override
    public void sunleBhai(Class<?> commandClass) throws ServiceNotFoundException {
        //System.out.println("sunle bhai"+commandClass.getName());
        if(commandClass.equals(EmergencyCommand.class)){
            setEmergencyMode();
        }
        else if (commandClass.equals(StationClosedCommand.class)){
            setStationClosedMode();
        } else if(commandClass.equals(InServiceBothCommand.class)){
            setInServiceBoth();

        }  else if(commandClass.equals(InServiceQRCommand.class)){
            setInServiceQR();
        } else if(commandClass.equals(InServiceCardCommand.class)){
            setInServiceCard();
        }else if(commandClass.equals(OutOfServiceCommand.class)){
            setOutOfService();
        }else if(commandClass.equals(TestCommand.class)){
            setTestMode();
        }else if(commandClass.equals(MaintenanceCommand.class)){
            setMaintenanceMode();
        } else if (commandClass.equals(NoStationModeCommand.class)) {
            noStationMode();
        } else if (commandClass.equals(InServiceNoSaleCommand.class)) {
            setInServiceNoSale();

        } else{
            throw new ServiceNotFoundException("Service not found");
        }
    }

    private void noStationMode() {
        agent.setEquipmentPrivilege(EquipmentMapper.mapToEquipmentPrivilege(agent.getEquipmentPrivilegeDto(),agent.getEquipmentPrivilege()));
        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.IN_SERVICE);
        agent.getGrpcApiListener().sendAlarm(Alarm.NO_STATION_MODE);
        agent.getGrpcApiListener().sendOperationMode(OperationMode.IN_SERVICE);
        agent.getGrpcApiListener().sendPeripheralStatus(agent.getDeviceStatusListener().getDeviceStatus());

    }

    private void setInServiceNoSale() {
        equipmentPrivilege.setQrTicketIssue(false);
        equipmentPrivilege.setQrTicketAnalysis(false);

        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.IN_SERVICE);
        agent.getGrpcApiListener().sendAlarm(Alarm.IN_SERVICE);
        agent.getGrpcApiListener().sendInServiceOperationMode(OperationMode.IN_SERVICE,false,false);
    }

    private void setMaintenanceMode() {
        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.MAINTENANCE);
        agent.getGrpcApiListener().sendAlarm(Alarm.MAINTENANCE_MODE);

    }

    private void setTestMode() {
        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.TEST);
        agent.getGrpcApiListener().sendAlarm(Alarm.TEST_MODE);


    }

    private void setOutOfService() {
        equipmentPrivilege.setQrTicketIssue(false);
        equipmentPrivilege.setQrTicketAnalysis(false);
        equipmentPrivilege.setQrTicketAdjustment(false);
        equipmentPrivilege.setQrTicketCancellation(false);
        equipmentPrivilege.setQrTicketRefund(false);
        equipmentPrivilege.setQrTicketReprint(false);
        equipmentPrivilege.setQrTicketReplacement(false);
        equipmentPrivilege.setQrFreeTicket(false);
        equipmentPrivilege.setQrPaidTicket(false);
        equipmentPrivilege.setTvm(false);

        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.OUT_OF_SERVICE);
        agent.getGrpcApiListener().sendAlarm(Alarm.OUT_OF_SERVICE);
        agent.getGrpcApiListener().sendOperationMode(OperationMode.OUT_OF_SERVICE);
    }

    //only card
    private void setInServiceCard() {
        equipmentPrivilege.setQrTicketIssue(false);


        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.IN_SERVICE);
        agent.getGrpcApiListener().sendAlarm(Alarm.IN_SERVICE_CARD);
        agent.getGrpcApiListener().sendInServiceOperationMode(OperationMode.IN_SERVICE,false,true);
    }

    //only qr
    private void setInServiceQR() {
        equipmentPrivilege.setQrTicketIssue(true);

        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.IN_SERVICE);
        agent.getGrpcApiListener().sendAlarm(Alarm.IN_SERVICE_QR);
        agent.getGrpcApiListener().sendInServiceOperationMode(OperationMode.IN_SERVICE,true,false);
    }

    //both qr and card
    private void setInServiceBoth() {
        noStationMode();

        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.IN_SERVICE);
        agent.getGrpcApiListener().sendAlarm(Alarm.IN_SERVICE_QR_CARD);
        agent.getGrpcApiListener().sendInServiceOperationMode(OperationMode.IN_SERVICE,true,true);
    }

    private void setStationClosedMode() {
        Logger.info("Station Closed Mode in function");
        equipmentPrivilege.setQrTicketIssue(false);
        equipmentPrivilege.setQrTicketAnalysis(false);
        equipmentPrivilege.setQrTicketAdjustment(false);
        equipmentPrivilege.setQrTicketCancellation(false);
        equipmentPrivilege.setQrTicketRefund(false);
        equipmentPrivilege.setQrTicketReprint(false);
        equipmentPrivilege.setQrTicketReplacement(false);
        equipmentPrivilege.setQrFreeTicket(false);
        equipmentPrivilege.setQrPaidTicket(false);
        equipmentPrivilege.setTvm(false);

        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.STATION_CLOSE);
        agent.getGrpcApiListener().sendAlarm(Alarm.STATION_CLOSE);
        agent.getGrpcApiListener().sendSpecialMode(SpecialMode.STATION_CLOSED_MODE);

    }

    private void setEmergencyMode() {
        equipmentPrivilege.setQrTicketIssue(false);
        equipmentPrivilege.setQrTicketAnalysis(false);
        equipmentPrivilege.setQrTicketAdjustment(false);
        equipmentPrivilege.setQrTicketCancellation(false);
        equipmentPrivilege.setQrTicketRefund(false);
        equipmentPrivilege.setQrTicketReprint(false);
        equipmentPrivilege.setQrTicketReplacement(false);
        equipmentPrivilege.setQrFreeTicket(false);
        equipmentPrivilege.setQrPaidTicket(false);
        equipmentPrivilege.setTvm(false);

        agent.getDeviceStatus().setDeviceOperationMode(DeviceOperationMode.EMERGENCY);
        agent.getGrpcApiListener().sendAlarm(Alarm.EMERGENCY);
        agent.getGrpcApiListener().sendSpecialMode(SpecialMode.EMERGENCY);
    }
}
