package com.amay.tvm.backend.entity;


import com.amay.tvm.backend.enums.ContainerId;
import com.google.protobuf.Timestamp;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class CoinAmountEntity {
    private String containerId;
    private int unitAmount;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
