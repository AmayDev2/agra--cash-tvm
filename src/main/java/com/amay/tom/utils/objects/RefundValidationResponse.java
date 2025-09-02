package com.amay.tom.utils.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RefundValidationResponse {
    private boolean isValid;
    private String message;
    private String transactionId;
    private String refundAmount;
    private String currency;
    private String timestamp;

    public RefundValidationResponse(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }
}
