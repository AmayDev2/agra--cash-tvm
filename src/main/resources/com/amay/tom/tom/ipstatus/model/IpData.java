package com.amay.tom.ipstatus.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class IpData implements Serializable {

    private  String ipAddress;
    private String ipName;
    private  boolean status;

    public String getStatusColour(boolean status){
        return status?"green":"red";
    }

}
