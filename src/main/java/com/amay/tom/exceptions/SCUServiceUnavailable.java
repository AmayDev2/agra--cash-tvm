package com.amay.tom.exceptions;

import com.amay.tom.grpc.scugrpc.ScuService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.tinylog.Logger;

public class SCUServiceUnavailable extends RuntimeException{

    private void printStackTrace(Throwable cause) {
        cause.printStackTrace();
    }
    private void printLog(String s) {
        Logger.error(s);
    }
    public SCUServiceUnavailable(String message) {
        super(message);
    }

    public SCUServiceUnavailable(String message, StatusRuntimeException exception) {
        super(message, exception.getCause());
        printLog(message);
        printStackTrace(exception.getCause());
    }


}
