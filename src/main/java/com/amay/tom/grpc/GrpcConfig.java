package com.amay.tom.grpc;

import com.amay.tom.utils.env.EnvFile;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
//import org.ticket.tg.ticketsGrpc;
//import org.unitral.module.FtpServiceGrpc;

public class GrpcConfig {
    static ManagedChannel channel;

    static{

        channel = ManagedChannelBuilder.forAddress(EnvFile.getCCUIpAddress(), EnvFile.getCCUPort())
                .usePlaintext()  // No TLS for local development
                .build();
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
