package com.amay.tvm.backend.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MaintenanceLog {
    private String event;
    private String status;
    private LocalDateTime timestamp;

    public MaintenanceLog(String event, String status, LocalDateTime timestamp) {
        this.event=event;
        this.status=status;
        this.timestamp=timestamp;
    }

    public String getEvent() {
        return event;
    }



    public String getStatus() {
        return status;
    }



    public String getTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
