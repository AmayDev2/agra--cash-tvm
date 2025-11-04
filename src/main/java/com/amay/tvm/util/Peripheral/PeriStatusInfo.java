package com.amay.tvm.util.Peripheral;

import javafx.beans.property.SimpleStringProperty;

public class PeriStatusInfo {
    private SimpleStringProperty device;
    private  SimpleStringProperty status;

    public PeriStatusInfo(SimpleStringProperty device, SimpleStringProperty status) {
        this.device = device;
        this.status = status;
    }

    public SimpleStringProperty getDevice() {
        return device;
    }

    public SimpleStringProperty getStatus() {
        return status;
    }

    public void setStatus(SimpleStringProperty status) {  // <-- add setter
        this.status = status;
    }
}
