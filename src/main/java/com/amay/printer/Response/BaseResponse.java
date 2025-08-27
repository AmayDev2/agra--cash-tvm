package com.amay.printer.Response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseResponse {
    private boolean isSuccess;
    private String error;
    private long timestampEpoch;
}
