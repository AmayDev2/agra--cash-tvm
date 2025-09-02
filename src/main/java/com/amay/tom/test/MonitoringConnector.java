package com.amay.tom.test;


import com.amay.tom.interceptor.AuthClientInterceptor;
import com.amay.tom.interceptor.ClientIdInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


import java.util.concurrent.TimeUnit;

public class MonitoringConnector {

    private final String target;
    private final int port;
    private ManagedChannel channel;

    public MonitoringConnector(String target, int port) {
        this.target = target;
        this.port = port;
        initializeChannel();
    }

    private void initializeChannel() {
        this.channel = ManagedChannelBuilder.forAddress(target, port)
                .intercept(new AuthClientInterceptor("1234"))
                .intercept(new ClientIdInterceptor("01010401"))
                .usePlaintext()  // No TLS for local development
                .keepAliveTimeout(2, TimeUnit.SECONDS)  // Set timeout for connection establishment
                .build();
    }

    public org.network.monitorandcontrol.MonitorAndControlGrpc.MonitorAndControlBlockingStub getBlockingStub() {
        return org.network.monitorandcontrol.MonitorAndControlGrpc.newBlockingStub(channel);
    }

    public org.network.monitorandcontrol.MonitorAndControlGrpc.MonitorAndControlStub getAsyncStub() {
        return org.network.monitorandcontrol.MonitorAndControlGrpc.newStub(channel);
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