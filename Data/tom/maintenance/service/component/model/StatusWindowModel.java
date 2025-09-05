package com.amay.tom.maintenance.service.component.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class StatusWindowModel {

    private boolean connected;
    private String relativeOperation;
    private String title;

    public StatusWindowModel(boolean connected, String relativeOperation, String title) {
        this.connected = connected;
        this.relativeOperation = relativeOperation;
        this.title = title;
    }

    //**********************
    private String permission;

}
