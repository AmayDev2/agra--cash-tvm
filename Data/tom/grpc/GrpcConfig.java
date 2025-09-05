package com.amay.tom.grpc;

import com.amay.tom.utils.env.EnvFile;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.transaction.qr.QrTransactionGrpc;
//import org.ticket.tg.ticketsGrpc;
//import org.unitral.module.FtpServiceGrpc;

public class GrpcConfig {
    static ManagedChannel channel;

    static{

        channel = ManagedChannelBuilder.forAddress(EnvFile.getCCUIpAddress(), EnvFile.getCCUPort())
                .usePlaintext()  // No TLS for local development
                .build();
    }

    public static QrTransactionGrpc.QrTransactionBlockingStub getBlockingStub(){
        return QrTransactionGrpc.newBlockingStub(channel);
    }

    public static QrTransactionGrpc.QrTransactionStub getAsyncStub(){
        return QrTransactionGrpc.newStub(channel);
    }

    public static QrTransactionGrpc.QrTransactionFutureStub getFutureStub(){
        return QrTransactionGrpc.newFutureStub(channel);
    }


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
