package com.amay.tvm.backend.entity;

import com.amay.tvm.backend.enums.FinanceOperation;
import java.sql.Timestamp;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;


//@Builder
@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class FinanceOperationEntity {
    private String shiftId;
    private FinanceOperation operationType;
    private int unitAmount;
    private int quantity;
    private Timestamp updatedAt;
    private Timestamp createdAt;
}
