package com.amay.tom.exceptions;

public class PaymentNotDoneException extends RuntimeException{

    public PaymentNotDoneException(String message){
        super(message);
    }

    public PaymentNotDoneException(String message, Throwable cause){
        super(message, cause);
    }

    public PaymentNotDoneException(Throwable cause){
        super(cause);
    }

    public PaymentNotDoneException(){
        super();
    }
}
