package com.amay.tom.service.payment2.impl;

import com.amay.tom.ViewFactory;
import com.amay.tom.enums.PayMethod;
import com.amay.tom.model.payment.PaymentResponse;
import com.amay.tom.service.payment2.PaymentMedia;
import com.amay.tvm.backend.enums.TransactionStatus;
import com.amay.tvm.backend.mapper.TransactionMapper;
import com.amay.tvm.backend.model.Transaction;
import com.amay.tvm.backend.repository.TransactionRepository;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.bnr.BNRListener;
import com.amay.tvm.controller.CashInsertProcessingController;
import com.amay.tvm.controller.SessionCompletion;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import org.tinylog.Logger;

import java.util.UUID;

public class CashPayment implements PaymentMedia {

    @Override
    public Object pay(double amount, String orderId, Object... args) {
        StackPane stackPane = (StackPane) args[0] ; // ✅ if first element is a StackPane
        TransactionRepository transactionRepository=(TransactionRepository) args[1];
        PaymentResponse paymentResponse=new PaymentResponse();
        try {
            System.out.println("Cash Payment: " + amount + " OrderId: " + orderId);
                 paymentResponse.setOrderId(orderId)
                .setAmount((int)amount)
                .setTransactionId("CASH_"+UUID.randomUUID())
                .setRemoteTransactionId(UUID.randomUUID().toString())
                .setPaymentMode(PayMethod.CASH.name())
                .setSuccess(false)
                .setStatus(TransactionStatus.PROCESSING.name());

            saveInDbPaymentInitialization(paymentResponse,transactionRepository);

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

            return paymentResponse.setStatus(TransactionStatus.SUCCESS.name()).setSuccess(true);
        }catch (Exception e){
            return  paymentResponse.setStatus(TransactionStatus.FAILED.name());
        }finally {
            saveInDbPaymentCompletion(paymentResponse,transactionRepository);
            Platform.runLater(()->{stackPane.getChildren().removeLast();});
        }
    }
}
