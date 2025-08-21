package com.amay.tom.service.payment2;

public class PaymentFactory {

    public static PaymentMedia getPaymentMedia(AbstractPaymentMethod paymentMethod) {
        return paymentMethod.createPaymentMedia();
    }
}
