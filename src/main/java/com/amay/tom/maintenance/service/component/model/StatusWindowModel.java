package com.amay.tom.maintenance.service.component.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.Label;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class StatusWindowModel {

    private boolean connected;
    private String relativeOperation;
    private String title;
    private List<String> list;

    public StatusWindowModel(boolean connected, String relativeOperation, String title) {
        this.connected = connected;
        this.relativeOperation = relativeOperation;
        this.title = title;
    }

    public StatusWindowModel(boolean connected, String relativeOperation, String title, List<String> list) {
        this(connected,relativeOperation,title);
        this.list=list;
    }

    //**********************
    private String permission;

}
