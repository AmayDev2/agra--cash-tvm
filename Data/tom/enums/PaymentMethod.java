package com.amay.tom.enums;

public enum PaymentMethod {

    CASH("Cash",1),
    CARD("Card",2),
    UPI("UPI",3),
    NETBANKING("Net Banking",4);

    private final String paymentMethod;

    PaymentMethod(String paymentMethod, int i) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
