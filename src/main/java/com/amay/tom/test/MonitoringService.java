package com.amay.tom.test;


import io.grpc.stub.StreamObserver;
import org.network.monitorandcontrol.tr.TRProtocol;

//reconnecting to ccu monitoring connector and handling the commands from server
public class MonitoringService {

    private org.network.monitorandcontrol.MonitorAndControlGrpc.MonitorAndControlStub asyncStub=null;
    private StreamObserver<TRProtocol> requestObserver=null;
//    private CommandHandler commandHandler=null;
    private MonitoringConnector ccuMonitoringConnector=null;
//    private ThreadPool threadPool=null;
//    private volatile ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

//    public ConnectionStatus getConnectionStatus() {
//        return connectionStatus;
//    }


    public MonitoringService(org.network.monitorandcontrol.MonitorAndControlGrpc.MonitorAndControlStub asyncStub,
//                                        IApplicationService applicationService,
                             MonitoringConnector ccuMonitoringConnector
//                                        ThreadPool threadPool
    ) {
        //System.out.println("GrpcControlMonitoringService constructor called");
        this.asyncStub=asyncStub;
        requestObserver = trStreamObserver();
//        commandHandler=new CommandHandler(applicationService);
        this.ccuMonitoringConnector=ccuMonitoringConnector;
//        this.threadPool=threadPool;
    }

    private void reconnect() {
        ccuMonitoringConnector.reconnect();
        this.asyncStub = ccuMonitoringConnector.getAsyncStub();
    }

//    private void notConnected() {
//        try{
//       //     //System.out.println("Monitoring service not connected, reconnecting...");
//            connectionStatus = ConnectionStatus.CONNECTING;
//            Thread.sleep(5000);
//        }catch (InterruptedException e){
//         //   Logger.error("Error in sleep: {}", e.getMessage());
//        }
////        //System.out.println("shutdown {}, terminated {}",
////                ccuMonitoringConnector.isChannelShutdown() ,
////                ccuMonitoringConnector.isChannelTerminated());
//
//        if (ccuMonitoringConnector.isChannelShutdown() || ccuMonitoringConnector.isChannelTerminated()) {
//            this.reconnect();
//        }
//        requestObserver = tomStreamObserver();
//  //      //System.out.println("Reconnected to monitoring service");
//        this.initialConnectionRequest(RequestHandler.getInitialRequest());
//    }

    public void sendMessage(org.network.monitorandcontrol.tr.TRProtocol message) {
       //System.out.println("Sending message to server: "+message);
        requestObserver.onNext(message);
    }

    public void initialConnectionRequest(org.network.monitorandcontrol.tr.TRProtocol message) {
   //     //System.out.println("Sending initial request to server: {}", message);
        requestObserver.onNext(message);
    }

    public void markComplete() {
        requestObserver.onCompleted();
    }

    private StreamObserver<org.network.monitorandcontrol.tr.TRProtocol>  trStreamObserver() {
//        connectionStatus = ConnectionStatus.CONNECTED;
     //   //System.out.println("Connection established with monitoring server");

        return asyncStub.trStream(new StreamObserver<org.network.monitorandcontrol.tr.TRProtocol>() {

            @Override
            public void onNext(org.network.monitorandcontrol.tr.TRProtocol value) {

                //System.out.println("Received command for TR from server: "+ value);
//                commandHandler.handleCommand(value.getCommandType(),value);
            }

            @Override
            public void onError(Throwable t){
           //     Logger.error("Error from server: {}", t.getMessage(), t);
//                connectionStatus = ConnectionStatus.DISCONNECTED;
//                t.printStackTrace();
//                if (t instanceof StatusRuntimeException statusException) {
//                    if (statusException.getStatus().getCode() == Status.Code.UNAVAILABLE
//                            || statusException.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED
//                            || statusException.getStatus().getCode() == Status.Code.UNKNOWN){
//           //             Logger.error("Server is unavailable: {}", t.getMessage());
//                        connectionStatus = ConnectionStatus.CONNECTING;
//                        runAsync(()->notConnected(),threadPool.getSingleThread());
//                    }
//                }
            }

            @Override
            public void onCompleted() {
//                connectionStatus = ConnectionStatus.DISCONNECTED;
                //System.out.println("Server has completed sending messages");
            }
        });
    }

    public void shutdown() {
        requestObserver.onCompleted();
        ccuMonitoringConnector.shutdown();
    }
}
