package com.amay.tom.model.session;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Shift {
    private String shiftId;
    private String operatorId;
    private String deviceId;
    private String deviceSerial;
    private LocalDateTime createdAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String serialNo;
    private String stationId;
    private String lineNo;
    private String reason;
    private String currentStatus;
    private LocalDateTime updatedAt;

}