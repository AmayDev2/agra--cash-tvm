package com.amay.tom.grpc.scugrpc;


import com.amay.tom.config.SystemConfig;
import com.amay.tom.interceptor.AuthClientInterceptor;
import com.amay.tom.interceptor.ClientIdInterceptor;
import com.amay.tom.interceptor.ClientLoggingInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.amaytechnosystems.TomTransactionServiceGrpc;

import java.util.concurrent.TimeUnit;

public class SCUGrpcConnector {

    private final String target;
    private final int port;
    private ManagedChannel channel;

    public SCUGrpcConnector(String target, int port) {
        this.target = target;
        this.port = port;
        initializeChannel();
    }

    private void initializeChannel() {
        this.channel = ManagedChannelBuilder.forAddress(target, port)
                .usePlaintext()  // No TLS for local development
                .intercept(new ClientLoggingInterceptor())  // Add token to headers
                .intercept(new ClientIdInterceptor(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId()))
                .intercept(new AuthClientInterceptor("1234"))
                .keepAliveTimeout(2, TimeUnit.SECONDS)  // Set timeout for connection establishment
                .build();
    }

    public TomTransactionServiceGrpc.TomTransactionServiceBlockingStub getBlockingStub() {
        return TomTransactionServiceGrpc.newBlockingStub(channel);
    }

    public TomTransactionServiceGrpc.TomTransactionServiceStub getAsyncStub() {
        return TomTransactionServiceGrpc.newStub(channel);
    }

    public TomTransactionServiceGrpc.TomTransactionServiceFutureStub getFutureStub() {
        return TomTransactionServiceGrpc.newFutureStub(channel);
    }

    public void reconnect() {
        shutdown();
        initializeChannel();
    }

    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public boolean isChannelShutdown() {
        return channel == null || channel.isShutdown();
    }

    public boolean isChannelTerminated() {
        return channel == null || channel.isTerminated();
    }
}
