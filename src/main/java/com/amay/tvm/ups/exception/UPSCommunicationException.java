package com.amay.tvm.ups.exception;

/**
 * Custom exception for UPS communication errors
 */
public class UPSCommunicationException extends Exception {

    public UPSCommunicationException(String message) {
        super(message);
    }

    public UPSCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
