package com.amay.tvm.bnr;

public interface BNRMessage {
    String CANCEL_TRYING="Trying to cancel the payment";
    String REFUSE_TO_ACCEPT = "Can't accept this,Please Collect...& Retry";
    String CHANGE_COLLECT = "Please collect your change";
    String CANCELED = "Transactions successfully canceled";
    String ROLLBACK = "Can't accept,...Please Collect your Cash";
}
