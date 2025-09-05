package com.amay.tom.repository;

public class PassengerData {
    private static PassengerData INSTANCE;

    public static PassengerData getInstance(){
        if(INSTANCE==null){
            INSTANCE=new PassengerData();

        }
        return INSTANCE;

    }

    private PassengerData(){

    }

}
