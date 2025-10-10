package com.amay.tom.config;

public interface URLS {
    String GET_ALL_USERS= "http://192.168.1.43:5000/api/v1/users/all";
    String GET_ALL_USER_WITH_PROFILE="http://192.168.1.43:5000/api/v1/users/raw/all";
    String GET_ALL_USER_PROFILE= "http://192.168.1.43:5000/api/v1/users/profiles/all";
    String GET_ALL_VERSION= "http://192.168.1.43:5000/api/v1/params/all";
    String GET_FARE_TABLE = "http://192.168.1.43:5000/api/v1/ops/fare/matrix" ;
    String GET_STATIONS = "http://192.168.1.43:5000/api/v1/topology/stations";
    String GET_TICKET_CONFIG = "http://192.168.1.43:5000/api/v1/ops/ticket-config/latest";
    String GET_BUSINESS_DAY= "http://192.168.1.43:5000/api/v1/ops/business-day";
    String PEAK_HOURS = "http://192.168.1.43:5000/api/v1/ops/peak-time";
    String CALENDER = "http://192.168.1.43:5000/api/v1/ops/calendar";
    String PRODUCT_DEFINITION = "http://192.168.1.43:5000/api/v1/ops/product-definition";
    String TOM_CONFIG = "http://192.168.1.43:5000/api/v1/topology/tom-config";
    String TVM_CONFIG = "http://192.168.1.43:5000/api/v1/topology/tvm-config";
}
