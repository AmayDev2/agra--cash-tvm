package com.amay.tom.exceptions;

import org.tinylog.Logger;

public class QRCodeListener extends RuntimeException{


    public QRCodeListener(String message) {

        super(message);

        Logger.error("QRCodeListener  : " + message);
    }

    public QRCodeListener(String message, Throwable cause) {
        super(message, cause);
        Logger.error("QRCodeListener : " + message);
    }

    public QRCodeListener(Throwable cause) {
        super(cause);
    }

    public QRCodeListener() {
        super();
    }
    
    
    
}
