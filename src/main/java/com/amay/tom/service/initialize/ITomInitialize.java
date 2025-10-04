package com.amay.tom.service.initialize;

import javafx.scene.Scene;

import java.io.IOException;

public interface ITomInitialize {
    boolean loadEnv();

    void getSQLightDBConnection() throws RuntimeException,InterruptedException;
    void getSQLDBConnection() throws RuntimeException, InterruptedException;

    void setSCUTransactionConnection() throws IOException;

    void setCCUTransactionConnection() throws IOException;

    void setSCUMonitoringService() throws IOException;

    void setCCUMonitoringService() throws IOException;



    boolean getFareTable(boolean isUpdate);

    boolean getFareTableVersion();
            boolean getFareTableUpdate();
            boolean getCalender();
            boolean getCalenderVersion();
            boolean getCalenderUpdate();
            boolean getApplicationUpdate();
            boolean getBlackListCards();

    boolean getUserDataTableVersion(boolean isUpdate);




    boolean deviceAuthentication();
    boolean deviceAuthorization();
    boolean deviceConfiguration();
    boolean isClockSynchronized();
    void synchronizeClock();

    void initializeThreadPool();   //must

    boolean getUserUpdatedTable(boolean isUpdate);

    boolean peripheralDeviceStatus();
    void deviceInitialization(Scene scene);
    void checkStationMode();
    boolean loadStations(boolean isUpdate);
    void pushRemainedDate();
}
