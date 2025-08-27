package com.amay.tom;

//import com.amay.tom.config.SecurityUtil;
import com.amay.tom.controller.TomInitializeViewController;
import com.amay.tom.database.DatabaseConnector;
import com.amay.tom.database.RedisConnectionPool;
import com.amay.tom.enums.DeviceOperationMode;
import com.amay.tom.pdu.controller.PDUController;
import com.amay.tom.pdu.controller.command.AbnormalStationModeCommand;
import com.amay.tom.pdu.controller.command.PDUCommandDispatcher;
import com.amay.tom.repository.QRDataArray;
import com.amay.tom.repository.TicketsRepository;
import com.amay.tom.service.tom.ApplicationService;
import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.systemcontrole.SystemControl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import java.io.IOException;


public class Main extends Application {
    private final TicketsRepository ticketsRepository = TicketsRepository.getInstance();
    private IApplicationService applicationService ;

    private static final TaggedLogger appLogger = Logger.tag("APPLICATION");
    private static final TaggedLogger bizLogger = Logger.tag("BUSINESS");


    @Override
    public void init() throws Exception {
        super.init();
//        EnvFile.loadEnv();
    }

    @Override
    public void stop() throws Exception {
        RedisConnectionPool.getJedisPool().close();
        DatabaseConnector.closeConnection();
        QRDataArray.createQRTicketFile();
        Logger.info("Application stopped😒😒🙌");

        super.stop();
    }

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        Logger.info("Application started");
        appLogger.info("Application init called");
        bizLogger.info("Application init called");
        applicationService=new ApplicationService(new SystemControl());

        System.out.println("java version: "+System.getProperty("java.version"));
        System.out.println("javafx.version: " + System.getProperty("javafx.version"));
        Package pkg = com.amay.tom.Main.class.getPackage();
        String title    = pkg.getImplementationTitle();
        String version  = pkg.getImplementationVersion();
        String vendor   = pkg.getImplementationVendor();

        System.out.printf(
                "Title = %s, Version = %s, Vendor = %s%n",
                title, version, vendor
        );



//        stage.setScene(new Scene(null, 800, 600, Color.web("#666970")));
//
//        stage.getScene().

        System.out.println("Application started 1");

//        new SecurityUtil().passwordTest();


//cristalScan

//        stage.setResizable(false);
//        stage.setFullScreen(true);
//        stage.setMaximized(true) ;
//        stage.setFullScreenExitHint("q to quit");
//        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));



//        Logger.info("Current Station Name: {}, Station ID: {}, Equipment Serial: {}, Equipment ID: {}",
//                SystemConfig.getInstance().getCurrentStation().getStationName(),
//                SystemConfig.getInstance().getCurrentStation().getStationId(),
//                SystemConfig.getInstance().getCurrentEquipment().getEquipmentSerial(),
//                SystemConfig.getInstance().getCurrentEquipment().getEquipmentId());

try {

    FXMLLoader fxmlLoader =new FXMLLoader(Main.class.getResource("initialize/tom-initialize-view.fxml"));
    fxmlLoader.setControllerFactory(param -> new TomInitializeViewController(applicationService));
    Scene scene = new Scene(fxmlLoader.load(), 1024, 1280);
//    scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
    scene.getStylesheets().add(getClass().getResource("/css/agra-theme.css").toExternalForm());

    // Add key event filter to prevent system keys
    scene.addEventFilter(KeyEvent.ANY, event -> {
        if (event.getCode() == KeyCode.WINDOWS || 
            event.getCode() == KeyCode.COMMAND ||
            event.getCode() == KeyCode.ALT ||
            event.getCode() == KeyCode.DELETE ||
            event.getCode() == KeyCode.ESCAPE ||
            (event.isAltDown() && event.getCode() == KeyCode.F4)){
            event.consume();
            Platform.runLater(() -> {
                stage.setFullScreen(true);
                stage.setAlwaysOnTop(true);
                stage.toFront();
            });
        }
    });

    // Prevent window from being minimized
    stage.iconifiedProperty().addListener((obs, wasIconified, isNowIconified) -> {
        if (isNowIconified) {
            Platform.runLater(() -> {
                stage.setIconified(false);
                stage.setFullScreen(true);
                stage.setAlwaysOnTop(true);
                stage.toFront();
            });
        }
    });

    // Keep window always on top
    stage.setAlwaysOnTop(true);
    
    // Set window properties
//    stage.setMinHeight(524);
//    stage.setMinWidth(1280);
//    stage.setMaxHeight(1024);
//    stage.setMaxWidth(1280);
    stage.setFullScreen(true);
    stage.setFullScreenExitHint(null);
    stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    stage.setTitle("TOM");
    stage.setScene(scene);

    // Add window closing event handler to prevent Alt+F4
    stage.setOnCloseRequest(event -> {
        event.consume();
        Platform.runLater(() -> {
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);
            stage.toFront();
        });
    });

    // Add window state change listener
    stage.setOnShown(event -> {
        Platform.runLater(() -> {
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);
            stage.toFront();
        });
    });

    // Add focus listener to maintain full screen
    stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
        if (isNowFocused) {
            Platform.runLater(() -> {
                stage.setFullScreen(true);
                stage.setAlwaysOnTop(true);
                stage.toFront();
            });
        }
    });

    // Add window state change listener
    stage.setOnHiding(event -> {
        Platform.runLater(() -> {
            stage.show();
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);
            stage.toFront();
        });
    });

    // Handle window state changes
    stage.iconifiedProperty().addListener((obs, wasIconified, isNowIconified) -> {
        if (isNowIconified) {
            Platform.runLater(() -> {
                stage.setIconified(false);
                stage.setFullScreen(true);
                stage.setAlwaysOnTop(true);
                stage.toFront();
            });
        }
    });

    // Add window state change listener for window state changes
    stage.setOnShowing(event -> {
        Platform.runLater(() -> {
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);
            stage.toFront();
        });
    });

    // Add window state change listener for window state changes
    stage.setOnHidden(event -> {
        Platform.runLater(() -> {
            stage.show();
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);
            stage.toFront();
        });
    });

    stage.show();

    var screens = javafx.stage.Screen.getScreens();

    if (screens.size() > 1) {
        Rectangle2D screen2Bounds = screens.get(1).getVisualBounds();

        FXMLLoader pduLoader = new FXMLLoader(Main.class.getResource("pdu/main_container.fxml"));


//        PDUController controller = pduLoader.getController();
//        Scene pduScene = new Scene(pduLoader.load(), 640, 480.0);
        Scene pduScene = new Scene(pduLoader.load(), 1280, 1024);
//        pduScene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pduScene.getStylesheets().add(getClass().getResource("/css/agra-theme.css").toExternalForm());
        PDUController pduController = pduLoader.getController();

        PDUCommandDispatcher.INSTANCE.setController(pduController);

        Stage pduStage = new Stage();
        pduStage.setScene(pduScene);
        pduStage.setTitle("TOM PDU");
        pduStage.setX(screen2Bounds.getMinX());
        pduStage.setY(screen2Bounds.getMinY());
        pduStage.setFullScreen(true);
//        pduStage.maxHeightProperty();
        pduStage.maximizedProperty();
//            pduStage.setAlwaysOnTop(true);
        pduStage.setFullScreenExitHint(null);
        pduStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        pduStage.initStyle(StageStyle.UNDECORATED);


        pduStage.show();

        PDUCommandDispatcher.INSTANCE.dispatch(new AbnormalStationModeCommand(DeviceOperationMode.SHIFT_NOT_ACTIVE));

//        new Thread(() -> {
//            try {
//                AtomicInteger i= new AtomicInteger();
//                while (true) {
//                    Thread.sleep(3000); // Update every second
//                    Platform.runLater(() -> {
//                        controller.setText("Initializing PDU controller..."+(i.incrementAndGet()));
//                    });
//                }
//
//            } catch (Exception e) {
//                Logger.error("Error initializing PDU controller: {}", e);
//                e.printStackTrace();
//            }
//        }).start();


    } else {
        Logger.warn("Second screen not detected. PDU screen will not be launched.");
    }

//    GrpcControlMonitoringService grpcControlMonitoringService = new GrpcControlMonitoringService(GrpcConfig.getAsyncStub(),applicationService);
//    grpcControlMonitoringService.initialConnectionRequest(RequestHandler.getInitialRequest());

}catch (Exception e){
//    invocationTargetException.getTargetException()
    Logger.error("Error in loading main scene: {}", e);
    e.printStackTrace();
}

//                stage.setFullScreen(true);
//}
    }

    public static void main(String[] args) throws IOException {
//        EnvFile.loadEnv();
        launch();

    }

}