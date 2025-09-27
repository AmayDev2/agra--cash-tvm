package com.amay.tom.service.api.impl;

import com.amay.tom.api.ApiClient;
import com.amay.tom.api.service.IApiRequest;
import com.amay.tom.api.service.impl.ApiRequest;
import com.amay.tom.config.URLS;
import com.amay.tom.model.PeripheralStatus;
import com.amay.tom.service.api.IApi;
import com.amay.tom.utils.helper.Helper;
import com.amay.tvm.backend.enums.LoggerTag;
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
            Logger.tag(LoggerTag.APP).error("{}", (Object) ex.getStackTrace());
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
//    https://192.168.1.43:5000/api/v1/params/all
    @Override
    public String getCalender() {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.CALENDER.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());
    }
//    http://localhost:5000/api/v1/ops/peak-times
    @Override
    public String getPeakTime() {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
         apiRequest.createGetRequest(URLS.PEAK_HOURS.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());
    }
//    http://localhost:5000/api/v1/ops/business-day
    @Override
    public String getBusinessDay() {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.GET_BUSINESS_DAY.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());
    }

    @Override
    public String getMasterVersion() {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.GET_ALL_VERSION.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());
    }

    @Override
    public String getProduct() {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.PRODUCT_DEFINITION.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());

    }

    public String getTomConfig() {
        ApiClient apiClient = new ApiClient();
        apiClient.setConnectionTimeout(2);
        apiClient.setReadTimeout(2);
        IApiRequest apiRequest = new ApiRequest();
        apiRequest.setHeader("Content-Type", "application/json");
        apiRequest.createGetRequest(URLS.TOM_CONFIG.replace("192.168.1.43:5000", IP + ":" + PORT));
        return apiClient.sendRequest(apiRequest.buildRequest());
    }
}
