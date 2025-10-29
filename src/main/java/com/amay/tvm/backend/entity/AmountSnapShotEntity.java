package com.amay.tvm.backend.entity;

import com.amay.printer.BNRLoadUnload;
import com.amay.tvm.backend.enums.ContainerId;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class AmountSnapShotEntity {
    private String shiftId;
    private ContainerId containerId;
    private int unitAmount;
    private int currentQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
