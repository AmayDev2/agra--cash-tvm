package com.amay.tom.model.session;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.time.LocalDateTime;
//please use setter chain or builder pattern to set the values of the fields!!
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShiftDto {
    private String shiftId;
    private String operatorId;
    private String deviceId;
    private String deviceSerial;
    private Timestamp createdAt;
    private Timestamp startTime;
    private Timestamp endTime;
    private String serialNo;
    private String stationId;
    private String lineNo;
    private String reason;
    private String currentStatus;
    private Timestamp updatedAt;

}
