package com.amay.tom.config;

import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.model.Equipment;
import com.amay.tom.model.station.Station;
import com.amay.tom.model.siftdata.User;

import java.io.IOException;

public class SystemConfig {

    private static volatile SystemConfig INSTANCE = null;
    private volatile Station currentStation;
    private volatile Equipment currentEquipment;
    private volatile User currentUser;


    private volatile DeviceOperationMode deviceCurrentStatus;

    private volatile String lineNumber;

    private boolean isRedisConnected = false;



//    private volatile deviceCurrentStatus;

    private SystemConfig() {
        // Private constructor to prevent instantiation

    }

    public static void getInstance(String stationId, String stationName, String equipmentId, String equipmentSerial, String lineNumber) {
        if (INSTANCE == null) {
            synchronized (SystemConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SystemConfig();
                    INSTANCE.currentEquipment = new Equipment(equipmentId, equipmentSerial);
                    INSTANCE.currentStation = new Station(stationId, stationName);
                    INSTANCE.lineNumber = lineNumber;
                }
            }
        }
//        return INSTANCE;
    }

    public static SystemConfig getInstance(){
        return INSTANCE;
    }

    public static long getDeviceStatusCheckInterval() {
        return 5000;
    }

    private static String lastOrderId;
    public static String setLastOrderId(String orderId) {
        lastOrderId = orderId;
        return lastOrderId;
    }

    public static String getLastOrderId() {
        System.out.println("Get last order id "+lastOrderId);
        return lastOrderId;
    }

    public Station getCurrentStation() {
        return currentStation;
    }

    public Equipment getCurrentEquipment() {
        return currentEquipment;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setDeviceCurrentStatus(DeviceOperationMode status) {
        this.deviceCurrentStatus = status;
    }

//    public DeviceStatus getDeviceCurrentStatus() {
//        return deviceCurrentStatus;
//    }

    public int getHeight(){
        return 1024;
    }

    public int getWidth(){
        return 1280;
    }


//    public  boolean getRedisStatus() {
//        return isRedisConnected;
//    }
    public void setRedisStatus(boolean status) {
        isRedisConnected = status;
    }

    public String getLineNumber() {
        return lineNumber;
    }


    public String getTomIp() {
        try{
            return java.net.InetAddress.getLocalHost().getHostAddress();
        }catch (IOException e) {
            e.printStackTrace();
            return "127.0.0.1";
        }
    }
}
