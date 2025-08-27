package com.amay.tom.grpc.monotoring;

import com.amay.tom.enums.ConnectionStatus;
import com.amay.tom.service.qrService2.DataPushService;
import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.threadpool.ThreadPool;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.network.monitorandcontrol.MonitorAndControlGrpc;
import org.network.monitorandcontrol.tom.TOMProtocol;
import org.tinylog.Logger;

import static java.util.concurrent.CompletableFuture.runAsync;

public class GrpcControlMonitoringService {

    private MonitorAndControlGrpc.MonitorAndControlStub asyncStub = null;
    private StreamObserver<TOMProtocol> requestObserver = null;
    private CommandHandler commandHandler = null;
    private CCUMonitoringConnector ccuMonitoringConnector = null;
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
        Logger.info("GrpcControlMonitoringService constructor called");
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
        this.requestObserver = tomStreamObserver();
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
        Logger.info("Connection status changed from {} to {} for channel: {}",
                oldStatus, status, chanelName);
    }

    // ADDED: Setup connection status listener
    private void setupConnectionStatusListener() {
        connectionStatus.addListener((observable, oldValue, newValue) -> {
            Logger.info("Connection status listener triggered: {} -> {} for channel: {}",
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
        Logger.info("✅ Successfully connected to monitoring service on channel: {}", chanelName);
        // Additional connected logic here
        dataPushService.pushData();
    }

    private void onDisconnected() {
        Logger.warn("❌ Disconnected from monitoring service on channel: {}", chanelName);
        // Additional disconnected logic here
    }

    private void onConnecting() {
        Logger.info("🔄 Attempting to connect to monitoring service on channel: {}", chanelName);
        // Additional connecting logic here
    }

    private void onConnectionError() {
        Logger.error("💥 Connection error occurred on channel: {}", chanelName);
        // Additional error handling logic here
    }

    private void reconnect() {
        try {
            setConnectionStatus(ConnectionStatus.CONNECTING);
            ccuMonitoringConnector.reconnect();
            this.asyncStub = ccuMonitoringConnector.getAsyncStub();
            Logger.info("Reconnection initiated for channel: {}", chanelName);
        } catch (Exception e) {
            Logger.error("Error during reconnection: {}", e.getMessage());
            setConnectionStatus(ConnectionStatus.ERROR);
        }
    }

    private void notConnected() {
        try {
            Logger.info("Monitoring service not connected, reconnecting...");
            setConnectionStatus(ConnectionStatus.CONNECTING); // FIXED: Use setter
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Logger.error("Error in sleep: {}", e.getMessage());
            Thread.currentThread().interrupt(); // ADDED: Proper interrupt handling
            return;
        }

        try {
            if (ccuMonitoringConnector.isChannelShutdown() || ccuMonitoringConnector.isChannelTerminated()) {
                this.reconnect();
            }

            requestObserver = tomStreamObserver();
            Logger.info("Reconnected to monitoring service");
            this.initialConnectionRequest(RequestHandler.getInitialRequest());

        } catch (Exception e) {
            Logger.error("Error during reconnection process: {}", e.getMessage());
            setConnectionStatus(ConnectionStatus.ERROR);
        }
    }

    public void sendMessage(TOMProtocol message) {
        try {
            if (requestObserver != null && getConnectionStatus() == ConnectionStatus.CONNECTED) {
                Logger.info("Sending message to server: {}", message);
                requestObserver.onNext(message);
            } else {
                Logger.warn("Cannot send message - not connected. Status: {}", getConnectionStatus());
            }
        } catch (Exception e) {
            Logger.error("Error sending message: {}", e.getMessage());
            setConnectionStatus(ConnectionStatus.ERROR);
        }
    }

    public void initialConnectionRequest(TOMProtocol message) {
        try {
            Logger.info("Sending initial request to server: {}", message);
            if (requestObserver != null) {
                requestObserver.onNext(message);
            } else {
                Logger.error("RequestObserver is null, cannot send initial request");
            }
        } catch (Exception e) {
            Logger.error("Error sending initial request: {}", e.getMessage());
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
            Logger.error("Error marking stream complete: {}", e.getMessage());
        }
    }

    private StreamObserver<TOMProtocol> tomStreamObserver() {
        Logger.info("Creating stream observer for monitoring server on channel: {}", chanelName);

        return asyncStub.tomStream(new ClientResponseObserver<TOMProtocol, TOMProtocol>() {

            @Override
            public void beforeStart(ClientCallStreamObserver<TOMProtocol> requestStream) {
                // Flow control handling
                requestStream.setOnReadyHandler(() -> {
                    if (requestStream.isReady()) {
                        Logger.info("✅ Stream is ready to send messages (onReady) for channel: {}", chanelName);
                        setConnectionStatus(ConnectionStatus.CONNECTED); // FIXED: Set to CONNECTED when ready
                    }
                });
            }

            @Override
            public void onNext(TOMProtocol value) {
                try {
                    Logger.info("Received command from server: {}", value);

                    // Ensure we're marked as connected when receiving messages
                    if (getConnectionStatus() != ConnectionStatus.CONNECTED) {
                        setConnectionStatus(ConnectionStatus.CONNECTED);
                    }

                    commandHandler.handleCommand(value.getCommandType(), value);
                } catch (Exception e) {
                    Logger.error("Error handling received command: {}", e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                Logger.error("Error from server: {} {}", t.getMessage(), chanelName, t);
                setConnectionStatus(ConnectionStatus.DISCONNECTED); // FIXED: Use setter

                if (t instanceof StatusRuntimeException statusException) {
                    Status.Code code = statusException.getStatus().getCode();
                    if (code == Status.Code.UNAVAILABLE
                            || code == Status.Code.DEADLINE_EXCEEDED
                            || code == Status.Code.UNKNOWN) {
                        Logger.error("Server is unavailable: {}", t.getMessage());
                        setConnectionStatus(ConnectionStatus.CONNECTING); // FIXED: Use setter
                        runAsync(() -> notConnected(), threadPool.getSingleThread());
                    } else {
                        setConnectionStatus(ConnectionStatus.ERROR);
                    }
                } else {
                    setConnectionStatus(ConnectionStatus.ERROR);
                }
            }

            @Override
            public void onCompleted() {
                setConnectionStatus(ConnectionStatus.DISCONNECTED); // FIXED: Use setter
                Logger.info("Server has completed sending messages for channel: {}", chanelName);
            }
        });
    }

    public void shutdown() {
        try {
            Logger.info("Shutting down GrpcControlMonitoringService for channel: {}", chanelName);
            setConnectionStatus(ConnectionStatus.DISCONNECTED);

            if (requestObserver != null) {
                requestObserver.onCompleted();
            }

            if (ccuMonitoringConnector != null) {
                ccuMonitoringConnector.shutdown();
            }

            Logger.info("GrpcControlMonitoringService shutdown complete for channel: {}", chanelName);
        } catch (Exception e) {
            Logger.error("Error during shutdown: {}", e.getMessage());
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
}
