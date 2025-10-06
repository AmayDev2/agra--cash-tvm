package com.amay.tom.model.siftdata;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShiftManagementDTO {
    private BooleanProperty selected=new SimpleBooleanProperty(false);
    private String shiftId;
    private String operatorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}