package com.amay.tom.model.session;


import com.amay.tvm.backend.entity.AmountSnapShotEntity;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<AmountSnapShotEntity> amountSnapShotEntityList;
    private List<FinanceOperationEntity> financeOperationEntityList;

    public Shift(String shiftId, String operatorId, String deviceId, String deviceSerial, LocalDateTime createdAt, LocalDateTime startTime, LocalDateTime endTime, String serialNo, String stationId, String lineNo, String reason, String currentStatus, LocalDateTime updatedAt, String imprest_money, String config_version, Timestamp ccu, Timestamp scu, String role) {
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