package com.amay.tom.grpc.monotoring;

import com.amay.tom.enums.ConnectionStatus;
import com.amay.tom.service.qrService2.DataPushService;
import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.threadpool.ThreadPool;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.network.monitorandcontrol.MonitorAndControlGrpc;
import org.network.monitorandcontrol.tom.TOMModeControl;
import org.network.monitorandcontrol.tom.TOMProtocol;
import org.network.monitorandcontrol.tvm.TVMProtocol;
import org.tinylog.Logger;

import static java.util.concurrent.CompletableFuture.runAsync;
//reconnecting to ccu monitoring connector and handling the commands from server
public class GrpcControlMonitoringService {

    private MonitorAndControlGrpc.MonitorAndControlStub asyncStub=null;
    private StreamObserver<TVMProtocol> requestObserver=null;
    private CommandHandler commandHandler=null;
    private CCUMonitoringConnector ccuMonitoringConnector=null;
    private ThreadPool threadPool=null;
    private volatile ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }


    public GrpcControlMonitoringService(MonitorAndControlGrpc.MonitorAndControlStub asyncStub,
                                        IApplicationService applicationService,
                                        CCUMonitoringConnector ccuMonitoringConnector,
                                        ThreadPool threadPool,
                                        String channelName,
                                        DataPushService dataPushService


    ) {
        Logger.info("GrpcControlMonitoringService constructor called");
        this.asyncStub=asyncStub;
        requestObserver = tvmStreamObserver();
        commandHandler=new CommandHandler(applicationService);
        this.ccuMonitoringConnector=ccuMonitoringConnector;
        this.threadPool=threadPool;
    }

    private void reconnect() {
        ccuMonitoringConnector.reconnect();
        this.asyncStub = ccuMonitoringConnector.getAsyncStub();
    }

    private void notConnected() {
        try{
            Logger.info("Monitoring service not connected, reconnecting...");
            connectionStatus = ConnectionStatus.CONNECTING;
            Thread.sleep(5000);
        }catch (InterruptedException e){
            Logger.error("Error in sleep: {}", e.getMessage());
        }
//        Logger.info("shutdown {}, terminated {}",
//                ccuMonitoringConnector.isChannelShutdown() ,
//                ccuMonitoringConnector.isChannelTerminated());

        if (ccuMonitoringConnector.isChannelShutdown() || ccuMonitoringConnector.isChannelTerminated()) {
            this.reconnect();
        }
        requestObserver = tvmStreamObserver();
        Logger.info("Reconnected to monitoring service");
        this.initialConnectionRequest(RequestHandler.getInitialRequest());
    }

    public void sendMessage(TVMProtocol message) {
        Logger.info("Sending message to server: {}", message);
        requestObserver.onNext(message);
    }

    public void initialConnectionRequest(TVMProtocol message) {
        Logger.info("Sending initial request to server: {}", message);
        requestObserver.onNext(message);
    }

    public void markComplete() {
        requestObserver.onCompleted();
    }

    private StreamObserver<TVMProtocol>  tvmStreamObserver() {
        connectionStatus = ConnectionStatus.CONNECTED;
        Logger.info("Connection established with monitoring server");

        return asyncStub.tvmStream(new StreamObserver<TVMProtocol>() {

            @Override
            public void onNext(TVMProtocol value) {

                Logger.info("Received command from server: {}", value);
                commandHandler.handleCommand(value.getCommandType(),value);
            }

            @Override
            public void onError(Throwable t){
                Logger.error("Error from server: {}", t.getMessage(), t);
                connectionStatus = ConnectionStatus.DISCONNECTED;
//                t.printStackTrace();
                if (t instanceof StatusRuntimeException statusException) {
                    if (statusException.getStatus().getCode() == Status.Code.UNAVAILABLE
                            || statusException.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED
                            || statusException.getStatus().getCode() == Status.Code.UNKNOWN){
                        Logger.error("Server is unavailable: {}", t.getMessage());
                        connectionStatus = ConnectionStatus.CONNECTING;
                        runAsync(()->notConnected(),threadPool.getSingleThread());
                    }
                }
            }

            @Override
            public void onCompleted() {
                connectionStatus = ConnectionStatus.DISCONNECTED;
                Logger.info("Server has completed sending messages");
            }
        });
    }

    public void shutdown() {
        requestObserver.onCompleted();
        ccuMonitoringConnector.shutdown();
    }
}