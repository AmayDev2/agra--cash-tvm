package com.amay.tvm.backend.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class AmountSnapShotEntity {
    private String shiftId;
    private String containerId;
    private int unitAmount;
    private int currentQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
