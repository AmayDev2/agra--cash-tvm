package com.amay.tom.exceptions;

public class NotRefundableOnDifferentStationException extends RuntimeException {
    public NotRefundableOnDifferentStationException(String message) {
        super(message);
    }

    public NotRefundableOnDifferentStationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotRefundableOnDifferentStationException(Throwable cause) {
        super(cause);
    }


}
