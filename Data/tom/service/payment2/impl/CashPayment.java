package com.amay.tom.service.payment2.impl;

import com.amay.tom.ViewFactory;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.service.payment2.PaymentMedia;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.bnr.BNRListener;
import com.amay.tvm.controller.CashInsertProcessingController;
import com.amay.tvm.controller.SessionCompletion;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class CashPayment implements PaymentMedia {

    @Override
    public Object pay(double amount, String orderId, Object... args) {
        StackPane stackPane= (StackPane) args[0];
        PaymentResponse paymentResponse=new PaymentResponse();
        try {
            System.out.println("Cash Payment: " + amount + " OrderId: " + orderId);

                 paymentResponse.setOrderId(orderId)
                .setAmount((int)amount)
                .setTransactionId("CASH_"+UUID.randomUUID())
                .setSuccess(false)
                .setStatus("FAILED");

            // Navigate to completion screen
            FXMLLoader fxmlLoader = ViewFactory.getCashPaymentView();
            CashInsertProcessingController cashInsertProcessingController=new CashInsertProcessingController((int)amount,stackPane);
            fxmlLoader.setControllerFactory((x)->cashInsertProcessingController);
            Platform.runLater(()->{try { stackPane.getChildren().add(fxmlLoader.load());}catch (Exception e){
                Logger.debug("BNR FAILED : "+e.getMessage());
                throw new RuntimeException(" Cash Insert View Couldn't load");
            }});

            boolean status=BNRIntegration.cashIn((int)amount,new BNRListener(cashInsertProcessingController));
            if(!status){
                throw new RuntimeException(" Transaction couldn't be succeed");
            }

            return paymentResponse.setStatus("SUCCESS").setSuccess(true);
        }catch (Exception e){
            return  paymentResponse.setStatus("FAILED");
        }finally {
            Platform.runLater(()->{stackPane.getChildren().removeLast();});
        }
    }
}
