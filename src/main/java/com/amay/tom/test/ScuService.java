package com.amay.tom.test;

import com.amay.tom.exceptions.SCUServiceUnavailable;
import com.amay.tom.grpc.scugrpc.SCUGrpcConnector;
import io.grpc.StatusRuntimeException;
import org.amaytechnosystems.*;
import org.tinylog.Logger;

import java.util.function.Supplier;

public class ScuService {

    private final SCUGrpcConnector scuGrpcConnector;
    private TRTransactionServiceGrpc.TRTransactionServiceBlockingStub blockingStub;

    // Constructor
    public ScuService(SCUGrpcConnector scuGrpcConnector,
                      TRTransactionServiceGrpc.TRTransactionServiceBlockingStub blockingStub) {
        this.scuGrpcConnector = scuGrpcConnector;
        this.blockingStub = blockingStub;
    }

    // Reconnect to the SCU service
//    private void reconnect() {
//        scuGrpcConnector.reconnect();
//        this.blockingStub = scuGrpcConnector.getBlockingStub();
//    }

    // Check if the SCU service is connected, and reconnect if necessary
//    private void ensureConnected() {
//        if (scuGrpcConnector.isChannelShutdown() || scuGrpcConnector.isChannelTerminated()) {
//            this.reconnect();
//        }
//    }

    // Retry the given task once if it fails due to SCU service unavailability
    private <T> T retryOnce(Supplier<T> task) {
        try {
            return task.get();  // Attempt the task and return its result
        } catch (StatusRuntimeException e) {
            Logger.warn("SCU service unavailable, retrying...", e);
            new SCUServiceUnavailable("SCU service unavailable", e);

            //TODO: Uncomment the following block if reconnection logic is needed
           /*  if (e.getStatus().getCode().equals(Status.Code.UNAVAILABLE)) {
                 this.reconnect();
             }*/

            try {
                return task.get();  // Retry once and return its result if successful
            } catch (StatusRuntimeException retryException) {
                Logger.error("Retry failed: SCU service unavailable", retryException);
                return null;  // Return null to indicate failure after retry
            }
        }
    }


    public TRTicketAnalysisResponseV1 getTicketAnalysisByNumber(TRTicketRequestV1 ticketAnalysisRequest){
        ATicket.newBuilder().build();
        return blockingStub.getTicketAnalysisDataByNumber(ticketAnalysisRequest);
    }


}
