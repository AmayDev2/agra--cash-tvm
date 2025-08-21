package com.amay.tom.service.payment2.absimpl;

import com.amay.tom.service.payment2.AbstractPaymentMethod;
import com.amay.tom.service.payment2.PaymentMedia;
import com.amay.tom.service.payment2.impl.CashPayment;

public class CashPaymentMethod extends AbstractPaymentMethod {

    @Override
    public PaymentMedia createPaymentMedia() {
        return new CashPayment();
    }
}
