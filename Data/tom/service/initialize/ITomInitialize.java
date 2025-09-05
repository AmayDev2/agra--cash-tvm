package com.amay.tom.service.initialize;

import java.io.IOException;

public interface ITomInitialize {
    boolean loadEnv();

    void getSQLightDBConnection() throws RuntimeException,InterruptedException;
    void getSQLDBConnection() throws RuntimeException, InterruptedException;

    void setCCUConnection() throws IOException;
    void setSCUConnection() throws IOException;

    void setCCUConnectionActual() throws IOException;
    void setCcuMonitoringService() throws IOException;

    void setCCUTGConnection() throws IOException;

    boolean versionCheck();

            boolean getFareTable();
            boolean getFareTableVersion();
            boolean getFareTableUpdate();
            boolean getCalender();
            boolean getCalenderVersion();
            boolean getCalenderUpdate();
            boolean getApplicationUpdate();
            boolean getBlackListCards();
            boolean getUserDataTableVersion();
            boolean getUserUpdatedTable();



    boolean deviceAuthentication();
    boolean deviceAuthorization();
    boolean deviceConfiguration();
    boolean isClockSynchronized();
    void synchronizeClock();

    void initializeThreadPool();   //must

    boolean peripheralDeviceStatus();
//    boolean deletePreviousData();
//    boolean deletePreviousLogs();
    void deviceInitialization();


    void checkStationMode();

    void loadStations();

    void pushRemainedDate();
}
