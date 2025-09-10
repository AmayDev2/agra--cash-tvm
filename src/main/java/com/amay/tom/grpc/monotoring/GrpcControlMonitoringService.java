package com.amay.tom.grpc.monotoring;

import com.amay.tom.enums.ConnectionStatus;
import com.amay.tom.grpc.monotoring.CCUMonitoringConnector;
import com.amay.tom.grpc.monotoring.CommandHandler;
import com.amay.tom.grpc.monotoring.RequestHandler;
import com.amay.tom.service.qrService2.DataPushService;
import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.threadpool.ThreadPool;
import com.amay.tvm.backend.enums.LoggerTag;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.extern.slf4j.Slf4j;
import org.network.monitorandcontrol.MonitorAndControlGrpc;
import org.network.monitorandcontrol.tvm.TVMProtocol;
import org.tinylog.Logger;

import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
public class GrpcControlMonitoringService {

    private MonitorAndControlGrpc.MonitorAndControlStub asyncStub = null;
    private StreamObserver<TVMProtocol> requestObserver = null;
    private com.amay.tom.grpc.monotoring.CommandHandler commandHandler = null;
    private com.amay.tom.grpc.monotoring.CCUMonitoringConnector ccuMonitoringConnector = null;
    private ThreadPool threadPool = null;

    // JavaFX property for connection status - FIXED
    private final ObjectProperty<ConnectionStatus> connectionStatus =
            new SimpleObjectProperty<>(this, "connectionStatus", ConnectionStatus.DISCONNECTED);

    private final String chanelName;
    private final DataPushService dataPushService;

    public GrpcControlMonitoringService(MonitorAndControlGrpc.MonitorAndControlStub asyncStub,
                                        IApplicationService applicationService,
                                        CCUMonitoringConnector ccuMonitoringConnector,
                                        ThreadPool threadPool, String chanelName,
                                        DataPushService dataPushService) {
        Logger.tag(LoggerTag.APP).debug("GrpcControlMonitoringService constructor called");
        this.chanelName = chanelName;
        this.asyncStub = asyncStub;
        this.ccuMonitoringConnector = ccuMonitoringConnector;
        this.threadPool = threadPool;
        this.dataPushService = dataPushService;

        // Initialize command handler
        this.commandHandler = new CommandHandler(applicationService);

        // Setup connection status listener
        setupConnectionStatusListener();

        // Initialize stream observer
        this.requestObserver = tvmStreamObserver();
    }

    // ADDED: Property getter for JavaFX binding
    public ObjectProperty<ConnectionStatus> connectionStatusProperty() {
        return connectionStatus;
    }

    // FIXED: Proper getter method
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus.get();
    }

    // ADDED: Setter method
    public void setConnectionStatus(ConnectionStatus status) {
        ConnectionStatus oldStatus = this.connectionStatus.get();
        this.connectionStatus.set(status);
        Logger.tag(LoggerTag.APP).debug("Connection status changed from {} to {} for channel: {}",
                oldStatus, status, chanelName);
    }

    // ADDED: Setup connection status listener
    private void setupConnectionStatusListener() {
        connectionStatus.addListener((observable, oldValue, newValue) -> {
            Logger.tag(LoggerTag.APP).debug("Connection status listener triggered: {} -> {} for channel: {}",
                    oldValue, newValue, chanelName);

            switch (newValue) {
                case CONNECTED -> onConnected();
                case DISCONNECTED -> onDisconnected();
                case CONNECTING -> onConnecting();
                case ERROR -> onConnectionError();
            }
        });
    }

    // ADDED: Connection status handlers
    private void onConnected() {
        Logger.tag(LoggerTag.APP).info("✅ Successfully connected to monitoring service on channel: {}", chanelName);
        this.initialConnectionRequest(RequestHandler.getInitialRequest());
        // Additional connected logic here
        dataPushService.pushData();
    }

    private void onDisconnected() {
        Logger.tag(LoggerTag.APP).warn("❌ Disconnected from monitoring service on channel: {}", chanelName);
        // Additional disconnected logic here
    }

    private void onConnecting() {
        Logger.tag(LoggerTag.APP).debug("🔄 Attempting to connect to monitoring service on channel: {}", chanelName);
        // Additional connecting logic here
    }

    private void onConnectionError() {
        Logger.tag(LoggerTag.APP).error("💥 Connection error occurred on channel: {}", chanelName);
        // Additional error handling logic here
    }

    private void reconnect() {
        try {
            setConnectionStatus(ConnectionStatus.CONNECTING);
            ccuMonitoringConnector.reconnect();
            this.asyncStub = ccuMonitoringConnector.getAsyncStub();
            Logger.tag(LoggerTag.APP).debug("Reconnection initiated for channel: {}", chanelName);
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error("Error during reconnection: {}", e.getMessage());
            setConnectionStatus(ConnectionStatus.ERROR);
        }
    }

    private void notConnected() {
        try {
            Logger.tag(LoggerTag.APP).debug("Monitoring service not connected, reconnecting...");
            setConnectionStatus(ConnectionStatus.CONNECTING); // FIXED: Use setter
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Logger.tag(LoggerTag.APP).error("Error in sleep: {}", e.getMessage());
            Thread.currentThread().interrupt(); // ADDED: Proper interrupt handling
            return;
        }

        try {
            if (ccuMonitoringConnector.isChannelShutdown() || ccuMonitoringConnector.isChannelTerminated()) {
                this.reconnect();
            }

            requestObserver = tvmStreamObserver();

        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error("Error during reconnection process: {}", e.getMessage());
            setConnectionStatus(ConnectionStatus.ERROR);
        }
    }

    public void sendMessage(TVMProtocol message) {
        try {
            if (requestObserver != null && getConnectionStatus() == ConnectionStatus.CONNECTED) {
                Logger.tag(LoggerTag.APP).debug("Sending message to server: {} [{}]", message,chanelName);
                requestObserver.onNext(message);
            } else {
                Logger.tag(LoggerTag.APP).warn("Cannot send message - not connected. Status: {} {}", getConnectionStatus(),chanelName);
            }
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error("Error sending message: {}", e.getMessage());
            setConnectionStatus(ConnectionStatus.ERROR);
        }
    }

    public void initialConnectionRequest(TVMProtocol message) {
        try {
            Logger.tag(LoggerTag.APP).debug("Sending initial request to server: {}", message);
            if (requestObserver != null) {
                requestObserver.onNext(message);
            } else {
                Logger.tag(LoggerTag.APP).error("RequestObserver is null, cannot send initial request");
            }
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error("Error sending initial request: {}", e.getMessage());
            setConnectionStatus(ConnectionStatus.ERROR);
        }
    }

    public void markComplete() {
        try {
            if (requestObserver != null) {
                requestObserver.onCompleted();
                setConnectionStatus(ConnectionStatus.DISCONNECTED);
            }
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error("Error marking stream complete: {}", e.getMessage());
        }
    }

    private StreamObserver<TVMProtocol> tvmStreamObserver() {
        Logger.tag(LoggerTag.APP).debug("Creating stream observer for monitoring server on channel: {}", chanelName);

        return asyncStub.tvmStream(new ClientResponseObserver<TVMProtocol, TVMProtocol>() {



            @Override
            public void beforeStart(ClientCallStreamObserver<TVMProtocol> requestStream) {
                // Flow control handling
                requestStream.setOnReadyHandler(() -> {
                    if (requestStream.isReady()) {
                        Logger.tag(LoggerTag.APP).debug("✅ Stream is ready to send messages (onReady) for channel: {}", chanelName);
                        setConnectionStatus(ConnectionStatus.CONNECTED); // FIXED: Set to CONNECTED when ready
                    }
                });
            }

            @Override
            public void onNext(TVMProtocol value) {
                try {
                    Logger.tag(LoggerTag.APP).debug("Received command from server: {}", value);

                    // Ensure we're marked as connected when receiving messages
                    if (getConnectionStatus() != ConnectionStatus.CONNECTED) {
                        setConnectionStatus(ConnectionStatus.CONNECTED);
                    }

                    commandHandler.handleCommand(value.getCommandType(), value);
                } catch (Exception e) {
                    Logger.tag(LoggerTag.APP).error("Error handling received command: {}", e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                Logger.tag(LoggerTag.APP).error("Error from server: {} {}", t.getMessage(), chanelName, t);
                setConnectionStatus(ConnectionStatus.DISCONNECTED); // FIXED: Use setter

                if (t instanceof StatusRuntimeException statusException) {
                    Status.Code code = statusException.getStatus().getCode();
                    if (code == Status.Code.UNAVAILABLE
                            || code == Status.Code.DEADLINE_EXCEEDED
                            || code == Status.Code.UNKNOWN) {
                        Logger.tag(LoggerTag.APP).error("Server is unavailable: {}", t.getMessage());
                        setConnectionStatus(ConnectionStatus.CONNECTING); // FIXED: Use setter
                        runAsync(() -> notConnected(), threadPool.getSingleThread());
                    } else {
                        setConnectionStatus(ConnectionStatus.DISCONNECTED);
                    }
                } else {
                    setConnectionStatus(ConnectionStatus.DISCONNECTED);
                }
            }

            @Override
            public void onCompleted() {
                setConnectionStatus(ConnectionStatus.DISCONNECTED); // FIXED: Use setter
                Logger.tag(LoggerTag.APP).debug("Server has completed sending messages for channel: {}", chanelName);
            }
        });
    }

    public void shutdown() {
        try {
            Logger.tag(LoggerTag.APP).debug("Shutting down GrpcControlMonitoringService for channel: {}", chanelName);
            setConnectionStatus(ConnectionStatus.DISCONNECTED);

            if (requestObserver != null) {
                requestObserver.onCompleted();
            }

            if (ccuMonitoringConnector != null) {
                ccuMonitoringConnector.shutdown();
            }

            Logger.tag(LoggerTag.APP).debug("GrpcControlMonitoringService shutdown complete for channel: {}", chanelName);
        } catch (Exception e) {
            Logger.tag(LoggerTag.APP).error("Error during shutdown: {}", e.getMessage());
        }
    }

    // ADDED: Method to add custom connection status listeners
    public void addConnectionStatusListener(javafx.beans.value.ChangeListener<ConnectionStatus> listener) {
        connectionStatus.addListener(listener);
    }

    // ADDED: Method to remove connection status listeners
    public void removeConnectionStatusListener(javafx.beans.value.ChangeListener<ConnectionStatus> listener) {
        connectionStatus.removeListener(listener);
    }

    public String getChanelName(){
        return chanelName;
    }
}
