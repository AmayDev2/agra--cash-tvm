package com.amay.tom.model.session;


import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
    private String imprest_money;
    private String config_version;
    private Timestamp ccu;
    private Timestamp scu;
    private String role;

}