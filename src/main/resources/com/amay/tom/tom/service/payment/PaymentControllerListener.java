package com.amay.tom.service.payment;

import com.amay.tom.controller.Controller;

public interface PaymentControllerListener {

    boolean waitForPayment();

    void setParentNode(Controller controller);
}
