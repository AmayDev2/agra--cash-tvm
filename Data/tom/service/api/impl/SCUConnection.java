package com.amay.tom.service.api.impl;

import com.amay.tom.api.ApiClient;
import com.amay.tom.api.service.IApiRequest;
import com.amay.tom.api.service.impl.ApiRequest;
import com.amay.tom.config.URLS;
import com.amay.tom.model.PeripheralStatus;
import com.amay.tom.service.api.IApi;
import com.amay.tom.utils.helper.Helper;
import org.tinylog.Logger;

import java.util.Objects;

public enum SCUConnection implements IApi {
    INSTANCE;
    private String IP;
    private String PORT;

    public void setIpPort(String IP,String PORT) {
        this.IP=IP;
        this.PORT=PORT;
    }

    SCUConnection() {

    }

    public void connect() {

    }

    public void disconnect() {
    }

    public void sendCommand(String command) {
    }

    public void sendNotification(String notification) {
    }

    public String sendDeviceStatus(int[] deviceStatus) {
        PeripheralStatus peripheralStatus = new PeripheralStatus(deviceStatus[0], deviceStatus[1], deviceStatus[2], deviceStatus[3]);
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        String json=Helper.ObjectToJson(peripheralStatus);
        Logger.debug("status to sent {}",json);
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createPostRequest("http://localhost:8080/api/monitor/status", json);
        return apiClient.sendRequest(apiRequest.buildRequest());

    }

    @Override
    public boolean checkVersion(String version) {

        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createPostRequest("http://localhost:8080/api/initialize/version", Helper.ObjectToJson(version));
        return Objects.equals(apiClient.sendRequest(apiRequest.buildRequest()), "true");
    }

    @Override
    public String getAllUsers() {
        try {
            ApiClient apiClient = new ApiClient();
            apiClient.setConnectionTimeout(2);
            apiClient.setReadTimeout(2);
            IApiRequest apiRequest = new ApiRequest();
            apiRequest.setHeader("Content-Type", "application/json");
            apiRequest.createGetRequest(URLS.GET_ALL_USERS.replace("192.168.1.43:5000", IP + ":" + PORT));
            return apiClient.sendRequest(apiRequest.buildRequest());
        }catch (RuntimeException ex){
            ex.printStackTrace();
        }
        return null;

    }

    @Override
    public String getAllUserWithProfile() {
        try {
            ApiClient apiClient = new ApiClient();
            apiClient.setConnectionTimeout(2);
            apiClient.setReadTimeout(2);
            IApiRequest apiRequest = new ApiRequest();
            apiRequest.setHeader("Content-Type", "application/json");
            apiRequest.createGetRequest(URLS.GET_ALL_USER_WITH_PROFILE.replace("192.168.1.43:5000", IP + ":" + PORT));
            return apiClient.sendRequest(apiRequest.buildRequest());
        }catch (RuntimeException ex){
            ex.printStackTrace();
        }
        return null;

    }

    @Override
    public String getUsersTableVersion() {

        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.GET_ALL_USERS.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());

    }

    @Override
    public String getUserProfile() {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.GET_ALL_USER_PROFILE.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());
    }

    @Override
    public String getFareTable() {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.GET_FARE_TABLE.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());
    }

    @Override
    public String getStations()  {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.GET_STATIONS.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());
    }

    @Override
    public String getTicketConfig() {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.GET_TICKET_CONFIG.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());
    }

    @Override
    public String getCalender() {
        return """
                [
                    {
                        "id": 1,
                        "configVer": "v0.1",
                        "specialDayName": "Test Special Day",
                        "businessDayModel": {
                            "id": 4,
                            "configVer": "v0.1",
                            "businessDayName": "Special Day One",
                            "dayType": "SPECIALDAY",
                            "startTime": "06:00:00",
                            "endTime": "22:00:00",
                            "fareMultiplier": 0.8,
                            "status": "ACTIVE"
                        },
                        "status": null,
                        "specialDate":"09-07"}
                ]
                """;
    }

    @Override
    public String getpeakTime() {
        return """
                
                [
                    {
                        "id": 52,
                        "configVer": "v0.1",
                        "peakTimeName": "Morning Peak",
                        "startTime": "08:00:00",
                        "endTime": "10:30:00",
                        "fareMultiplier": 1.1,
                        "status": "ACTIVE"
                    },
                    {
                        "id": 53,
                        "configVer": "v0.1",
                        "peakTimeName": "Afternoon Off-Peak",
                        "startTime": "13:00:00",
                        "endTime": "15:00:00",
                        "fareMultiplier": 0.9,
                        "status": "ACTIVE"
                    },
                    {
                        "id": 54,
                        "configVer": "v0.1",
                        "peakTimeName": "Evening Peak",
                        "startTime": "16:30:00",
                        "endTime": "20:00:00",
                        "fareMultiplier": 1.1,
                        "status": "ACTIVE"
                }
                ]
                """;
    }

    @Override
    public String getBusinessDay() {
        return """
                [
                    {
                        "id": 202,
                        "configVer": "v0.1",
                        "businessDayName": "Operation Weekday",
                        "dayType": "WEEKDAYS",
                        "startTime": "06:00:00",
                        "endTime": "21:00:00",
                        "fareMultiplier": 1,
                        "status": "ACTIVE"
                    },
                    {
                        "id": 203,
                        "configVer": "v0.1",
                        "businessDayName": "Special Day One",
                        "dayType": "SPECIALDAY",
                        "startTime": "06:00:00",
                        "endTime": "22:00:00",
                        "fareMultiplier": 1,
                        "status": "ACTIVE"
                    },
                    {
                        "id": 204,
                        "configVer": "v0.1",
                        "businessDayName": "Operation Saturday",
                        "dayType": "SATURDAY",
                        "startTime": "06:00:00",
                        "endTime": "21:00:00",
                        "fareMultiplier": 1.1,
                        "status": "ACTIVE"
                    },
                    {
                        "id": 205,
                        "configVer": "v0.1",
                        "businessDayName": "Operational Sundays",
                        "dayType": "SUNDAY",
                        "startTime": "06:00:00",
                        "endTime": "22:00:00",
                        "fareMultiplier": 0.9,
                        "status": "ACTIVE"
                }
                ]
                """;
    }
}
