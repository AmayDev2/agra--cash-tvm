package com.amay.tom.exceptions;

public class ShiftNotStartException extends RuntimeException{

    public ShiftNotStartException(String exception, Exception usernameNotFoundException){
        super(exception);
    }
}
