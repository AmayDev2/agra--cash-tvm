package com.amay.tom.model.session;



import com.amay.tvm.backend.dto.CashInventoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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
    private String imprest_money;
    private String config_version;
    private Timestamp ccu;
    private Timestamp scu;
    private String role;
    private List<CashInventoryDto> cashInventoryDtoList;

    public ShiftDto(
            String shiftId,
            String operatorId,
            String deviceId,
            String deviceSerial,
            Timestamp createdAt,
            Timestamp startTime,
            Timestamp endTime,
            String serialNo,
            String stationId,
            String lineNo,
            String reason,
            String currentStatus,
            Timestamp updatedAt,
            String imprest_money,
            String config_version,
            Timestamp ccu,
            Timestamp scu,
            String role
    ) {
        this.shiftId = shiftId;
        this.operatorId = operatorId;
        this.deviceId = deviceId;
        this.deviceSerial = deviceSerial;
        this.createdAt = createdAt;
        this.startTime = startTime;
        this.endTime = endTime;
        this.serialNo = serialNo;
        this.stationId = stationId;
        this.lineNo = lineNo;
        this.reason = reason;
        this.currentStatus = currentStatus;
        this.updatedAt = updatedAt;
        this.imprest_money = imprest_money;
        this.config_version = config_version;
        this.ccu = ccu;
        this.scu = scu;
        this.role = role;
    }

}
