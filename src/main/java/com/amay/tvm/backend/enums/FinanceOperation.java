package com.amay.tvm.backend.enums;

public enum FinanceOperation {
    BNR_LOAD("BNR LOAD", "01"),
    BNR_UNLOAD("BNR UNLOAD", "02"),
    COIN_LOAD("COIN LOAD", "03"),
    COIN_UNLOAD("COIN UNLOAD", "04"),
    BNR_DEPOSIT("BNR DEPOSIT", "05"),
    BNR_DISPENSE("BNR DISPENSE", "06"),
    BNR_NOT_COMMITTED("BNR NOT COMMITTED", "07"),
    COIN_DISPENSE("COIN DISPENSE", "08");

    private final String title;
    private final String value;

    FinanceOperation(String Title, String value) {
        this.title = Title;
        this.value = value;

    }

    public String getTitle() {
        return title;
    }
    public String getValue() {
        return value;
    }
    public static String getTitle(String value){
        for(FinanceOperation financeOperation :FinanceOperation.values()){
            if(financeOperation.value.equals(value)) return financeOperation.title;
        }
        throw new IllegalArgumentException("No Instance with given value : "+value+" found");
    }

}
