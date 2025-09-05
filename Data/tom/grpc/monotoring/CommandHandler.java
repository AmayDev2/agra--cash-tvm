package com.amay.tom.grpc.monotoring;

import com.amay.tom.enums.Command;
import com.amay.tom.model.RedisMessage;
import com.amay.tom.service.events.Remote;
import com.amay.tom.service.events.TOMCommand;
import com.amay.tom.service.events.commands.*;
import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.utils.helper.Helper;
import com.google.protobuf.Any;
import org.network.monitorandcontrol.tvm.TVMModeControl;

public class CommandHandler {

    private Remote remote;

    private final IApplicationService applicationService;

    public CommandHandler(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public void handleCommand(org.network.monitorandcontrol.CommandType commandType, org.network.monitorandcontrol.tvm.TVMProtocol value) {
        switch (commandType){
            case GET_DEVICE_INFO:
                System.out.println("GET_DEVICE_INFO");
                remote= new Remote(new DeviceInfoCommand(applicationService));
                break;
            case MODE_CONTROL:
                System.out.println("MODE_CONTROL");
                handelModeControl(value.getRequestData());
                break;
            case GET_DIVICE_VERSIONS:
                remote=new Remote(new VersionCommand(applicationService));
                System.out.println("GET_DIVICE_VERSIONS");
                break;
            case GET_PERIPHERAL_STATUS:
                remote=new Remote(new PheStatusCommand(applicationService));
                System.out.println("GET_PERIPHERAL_STATUS");
                break;
            default:
                System.out.println("UNKNOWN : "+commandType);

        }
        remote.pressButton();
    }

    private void handelModeControl(Any requestData) {
        try {
//            TOMModeControl tomModeControl = requestData.unpack(Any.class).unpack(TOMModeControl.class);
            TVMModeControl tomModeControl = requestData.unpack(TVMModeControl.class);
            System.out.println("Mode Control: " + tomModeControl);
            System.out.println("Operation Mode : "+tomModeControl.getOperationMode()+"  - "+"Special Mode : "+tomModeControl.getSpecialMode()+"qr"+tomModeControl.getQrSaleMode()+"card"+tomModeControl.getCardProcessMode());
            String commands=tomModeControl.toString();
            if(commands.contains("special_mode")){
            switch (tomModeControl.getSpecialMode()) {
                case SHUT_DOWN:
                    remote = new Remote(new ShutdownCommand(applicationService));
                    break;
                case RESTART:
                    remote = new Remote(new RebootCommand(applicationService));
                    break;
                case SHIFT_END:
                    remote = new Remote(new EOSCommand(applicationService));
                    break;
                case EMERGENCY:
                    remote = new Remote(new EmergencyCommand(applicationService));
                    break;
                case STATION_CLOSED_MODE:
                    remote = new Remote(new StationClosedCommand(applicationService));
                    break;
                case STATION_NORMAL:
                    remote = new Remote(new NoStationModeCommand(applicationService));
                    break;
                default:
                    System.out.println("UNKNOWN_COMMAND");
            }}else {
                switch (tomModeControl.getOperationMode()) {
                    case IN_SERVICE:
                        if(tomModeControl.getQrSaleMode() && tomModeControl.getCardProcessMode()){
                            remote = new Remote(new InServiceBothCommand(applicationService));
                        }else if(tomModeControl.getQrSaleMode()){
                            remote = new Remote(new InServiceQRCommand(applicationService));
                        }else if(tomModeControl.getCardProcessMode()){
                            remote = new Remote(new InServiceCardCommand(applicationService));
                        }else{
                            remote = new Remote(new InServiceNoSaleCommand(applicationService));
                        }
                        break;
                    case MAINTENANCE:
                        remote = new Remote(new RebootCommand(applicationService));
                        break;
                    case OUT_OF_SERVICE:
                        remote = new Remote(new OutOfServiceCommand(applicationService));
                        break;
                    case TEST:
                        remote = new Remote(new TestCommand(applicationService) {
                        });
                        break;
                    default:
                        System.out.println("UNKNOWN_COMMAND");

            }}
        }catch (Exception e){
            System.out.println("Error in handelModeControl: " + e.getMessage());
        }


    }


    private void sendCommand(String message) {

        System.out.println("Received command: " + RedisMessage.class + "\n");

        RedisMessage redisMessage= (RedisMessage) Helper.JSONtoObject(message, RedisMessage.class);
        assert redisMessage != null;
        Command command = Command.valueOf(redisMessage.getMessage());
        TOMCommand tomCommand ;
        switch(command){
            case END_OF_SHIFT :
                remote= new Remote(new EOSCommand(applicationService));
                break;
            case EMERGENCY_MODE:
//                tomCommand = new (applicationService);
                break;
            case PAUSE_SHIFT:
//                remote.endOfDay();
                break;
            case NORMAL_MODE:
//                remote.normalMode();
                break;
            default:
                System.out.println("Received command: " + message + "\n");
        }

        remote.pressButton();


    }
}
