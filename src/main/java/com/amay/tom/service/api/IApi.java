package com.amay.tom.service.api;

public interface IApi {
    public void setIpPort(String IP,String PORT);
    public String sendDeviceStatus(int[] deviceStatus);

    public boolean checkVersion(String version);

    public String getAllUsers();

    String getAllUserWithProfile();

    public String getUsersTableVersion();

    String getUserProfile();

    String getFareTable();

    String getStations();

    String getTicketConfig();

    String getCalender();

    String getPeakTime();

    String getBusinessDay();

    String getMasterVersion();

    String getProduct();

    String getTomConfig();
    String getTvmConfig();

    String getEquipmentDetails();
}
