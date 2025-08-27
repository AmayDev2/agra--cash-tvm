package com.amay.tom.grpc.ccugrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class CCUGrpcConnector {

    private final String target;
    private final int port;
    private ManagedChannel channel;

    public CCUGrpcConnector(String target, int port) {
        this.target = target;
        this.port = port;
        initializeChannel();
    }

    private void initializeChannel() {
        this.channel = ManagedChannelBuilder.forAddress(target, port)
                .usePlaintext()  // No TLS for local development
                .keepAliveTimeout(2, TimeUnit.SECONDS)  // Set timeout for connection establishment
                .build();
    }


    public void reconnect() {
        this.shutdown();
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


