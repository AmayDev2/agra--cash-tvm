package com.amay.tom.model.analysis;

import com.amay.tom.enums.AdjustmentType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.amaytechnosystems.AdjustmentArea;

import java.util.List;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class AdjustmentDetailDTO {
    private String adjustId;
    private String ticketId;
    private int amount;
    private String reason;
    private List<AdjustmentType> adjustmentTypes;
    private AdjustmentArea adjustmentArea;
    private String adjustmentTime;
}

