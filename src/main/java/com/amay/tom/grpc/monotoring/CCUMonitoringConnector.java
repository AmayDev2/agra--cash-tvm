package com.amay.tom.grpc.monotoring;

import com.amay.tom.config.SystemConfig;
import com.amay.tom.interceptor.AuthClientInterceptor;
import com.amay.tom.interceptor.ClientIdInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.h2.util.json.JSONStringTarget;
import org.network.monitorandcontrol.MonitorAndControlGrpc;

import java.util.concurrent.TimeUnit;

public class CCUMonitoringConnector {

    private final String target;
    private final int port;
    private ManagedChannel channel;
    private final boolean isCCU;

    public CCUMonitoringConnector(String target, int port,boolean isCCU) {
        this.target = target;
        this.port = port;
        this.isCCU = isCCU;
        initializeChannel(target,port);

    }

    private void initializeChannel(String target,int port) {
//        if(isCCU){
//            this.channel = ManagedChannelBuilder.forAddress(target, port)
//                    .intercept(new AuthClientInterceptor("1234"))
//                    .intercept(new ClientIdInterceptor(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId()))
//                    .usePlaintext()  // No TLS for local development
//                    .keepAliveTimeout(5, TimeUnit.SECONDS)       // wait 5 seconds for ACK
////                    .keepAliveTime(5, TimeUnit.SECONDS)         // send ping every 15 seconds
////                    .keepAliveWithoutCalls(true)
//                    .build();
//        }else
        this.channel = ManagedChannelBuilder.forAddress(target, port)
                .intercept(new AuthClientInterceptor("1234"))
                .intercept(new ClientIdInterceptor(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId()))
                .usePlaintext()  // No TLS for local development
                .keepAliveTimeout(5, TimeUnit.SECONDS)       // wait 5 seconds for ACK
                .keepAliveTime(5, TimeUnit.SECONDS)         // send ping every 15 seconds
                .keepAliveWithoutCalls(true)
                .build();
    }

    public MonitorAndControlGrpc.MonitorAndControlBlockingStub getBlockingStub() {
        return MonitorAndControlGrpc.newBlockingStub(channel);
    }

    public MonitorAndControlGrpc.MonitorAndControlStub getAsyncStub() {
        return MonitorAndControlGrpc.newStub(channel);
    }

    public MonitorAndControlGrpc.MonitorAndControlFutureStub getFutureStub() {
        return MonitorAndControlGrpc.newFutureStub(channel);
    }

    public void reconnect() {
        shutdown();
        initializeChannel(this.target, this.port);
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
