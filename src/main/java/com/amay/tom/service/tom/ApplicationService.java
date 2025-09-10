package com.amay.tom.service.tom;

import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.service.tom.RemoteListener;
import com.amay.tom.systemcontrole.SystemControl;
import org.tinylog.Logger;

import javax.management.ServiceNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationService  implements IApplicationService {

//    private ShiftService shiftService;
//    private final SiftService siftService;
//    private final GrpcApiListener grpcApiListener;
    private final SystemControl systemControl;

    private final List<com.amay.tom.service.tom.RemoteListener> remoteListeners=new ArrayList<>();

    public void addListener(RemoteListener remoteListener){
        this.remoteListeners.add(remoteListener);
    }
    //1- shift listener
    //2- grpc api listener
    //3- special command listener

    public void notifyListener(Class<?> listenerClass, Class<?> commandClass)  {
        remoteListeners.stream()
                .filter(listenerClass :: isInstance)
                .forEach(listener -> {
                    try {
                         listener.sunleBhai(commandClass);
                    } catch (ServiceNotFoundException e) {
                        e.printStackTrace();
                    }
                });
    }

    public ApplicationService(SystemControl systemControl) {
        this.systemControl = systemControl;
//        this.siftService = ImplSiftService.INSTANCE;
//        this.grpcApiListener = GrpcApiListener.INSTANCE;
    }
    @Override
    public boolean endOfShift() {

        Logger.info("End of shift");
//        shiftService.endOfShift(EOSType.SCU);
        return false;
    }

    @Override
    public boolean pauseShift() {

        Logger.info("Pause shift");
        return false;
    }

    @Override
    public boolean setMode() {
        Logger.info("Set mode");
        return false;
    }

    @Override
    public boolean sendPheStatus() {

        Logger.info("Send PHE status");
        return false;
    }

    @Override
    public boolean sendCurrentMode() {
        Logger.info("Send current mode");
        return false;
    }

    @Override
    public boolean sendVersion() {

        Logger.info("Send version");
        return false;
    }

    @Override
    public boolean deviceInformation() {

        Logger.info("Device information");
        return false;
    }

    @Override
    public boolean sendDeviceInfo() {
        Logger.info("Send device info");
//        this.grpcApiListener.sendDeviceInfo();
        return false;
    }

    @Override
    public boolean systemShutdown() {
        Logger.info("System shutdown");
        return this.systemControl.shutdownSystem();

    }

    @Override
    public boolean systemReboot() {
        Logger.info("System reboot");
        return this.systemControl.restartSystem();

    }

    @Override
    public void appClose() {

        this.systemControl.closeApplication();
    }

    @Override
    public void emergency() {
        Logger.info("Emergency");
    }


}
