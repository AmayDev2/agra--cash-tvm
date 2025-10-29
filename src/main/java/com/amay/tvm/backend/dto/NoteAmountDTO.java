package com.amay.tvm.backend.dto;

import com.amay.tvm.backend.enums.ContainerId;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
@ToString
public class NoteAmountDTO {
    private ContainerId containerId;
    private int unitAmount;
    private int cashInQuantity;
    private int cashOutQuantity;
    private int currentQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

