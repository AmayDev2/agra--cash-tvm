package com.amay.tom.model.siftdata;


import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ShiftDetail {
    
    private int shiftId;
    private Timestamp shiftStart;
    private Timestamp shiftEnd;
    private String stationId;
    private String equipmentId;
    private String operatorId;
    private String shiftStatus;

public ShiftDetail(Timestamp shiftStart, Timestamp shiftEnd, String stationId, String equipmentId, String operatorId, String shiftStatus) {
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.stationId = stationId;
        this.equipmentId = equipmentId;
        this.operatorId = operatorId;
        this.shiftStatus = shiftStatus;
    }



}
