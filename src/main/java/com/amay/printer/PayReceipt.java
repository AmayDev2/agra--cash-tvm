package com.amay.printer;

public abstract class PayReceipt {
    private String dateTime;
    private String rgId;
    private String Status;
    private String amount;
    private String paymentMethod;
    public abstract String formatedText();
}
