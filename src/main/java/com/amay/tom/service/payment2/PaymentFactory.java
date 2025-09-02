package com.amay.tom.service.payment2;

import com.amay.tom.service.payment2.AbstractPaymentMethod;
import com.amay.tom.service.payment2.PaymentMedia;

public class PaymentFactory {

    public static PaymentMedia getPaymentMedia(AbstractPaymentMethod paymentMethod) {
        return paymentMethod.createPaymentMedia();
    }
}
