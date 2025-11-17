package com.amay.tom;

import javafx.fxml.FXMLLoader;
import org.tinylog.Logger;

public enum ViewFactory {
    INSTANTE;

    public  FXMLLoader getMaintenanceLog() {
        return new FXMLLoader(javax.swing.text.ViewFactory.class.getResource("/com/amay/tom/maintenance/Alarm.fxml"));
    }
}

