package com.amay.tom.service.tom;

public interface IApplicationService {



    void addListener(RemoteListener remoteListener);
    void notifyListener(Class<?> className, Class<?> commandClass);
    boolean endOfShift();

    boolean pauseShift();

    boolean setMode();

    boolean sendCurrentMode();

    boolean sendPheStatus();

    boolean sendVersion();

    boolean deviceInformation();

    boolean sendDeviceInfo();

    boolean systemShutdown();

    boolean systemReboot();

    void appClose();

    void emergency();

//    void setShiftService(ShiftService shiftService);
}
