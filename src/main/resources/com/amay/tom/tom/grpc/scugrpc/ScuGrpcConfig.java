package com.amay.tom.grpc.scugrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.amaytechnosystems.TomTransactionServiceGrpc;
//import org.ticket.tg.ticketsGrpc;
//import org.unitral.module.FtpServiceGrpc;

@Deprecated
public class ScuGrpcConfig {
    static ManagedChannel channel;

    static{

        channel = ManagedChannelBuilder.forAddress("localhost", 9000)
                .usePlaintext()  // No TLS for local development
                .build();
    }

    public static TomTransactionServiceGrpc.TomTransactionServiceBlockingStub getBlockingStub(){
        return  TomTransactionServiceGrpc.newBlockingStub(channel);
    }

//    public static QrTransactionGrpc.QrTransactionStub getAsyncStub(){
//        return QrTransactionGrpc.newStub(channel);
//    }
//
//    public static QrTransactionGrpc.QrTransactionFutureStub getFutureStub(){
//        return QrTransactionGrpc.newFutureStub(channel);
//    }


//    public static FtpServiceGrpc.FtpServiceBlockingStub getFileBlockingStub(){
//        return FtpServiceGrpc.newBlockingStub(channel);
//    }
//
//    public static FtpServiceGrpc.FtpServiceStub getFileAsyncStub(){
//        return FtpServiceGrpc.newStub(channel);
//    }
//
//    public static FtpServiceGrpc.FtpServiceFutureStub getFileFutureStub(){
//        return FtpServiceGrpc.newFutureStub(channel);
//    }

    public static void shutdown(){
        channel.shutdown();
    }
}
