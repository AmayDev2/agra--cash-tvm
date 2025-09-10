package com.amay.tom;

//import com.amay.tom.config.SecurityUtil;
//import com.amay.tom.controller.PDUController;
import com.amay.printer.Printer;
import com.amay.tom.controller.TomInitializeViewController;
import com.amay.tom.database.DatabaseConnector;
import com.amay.tom.database.RedisConnectionPool;
import com.amay.tom.repository.QRDataArray;
import com.amay.tom.repository.TicketsRepository;
import com.amay.tom.service.tom.ApplicationService;
import com.amay.tom.service.tom.IApplicationService;
import com.amay.tom.systemcontrole.SystemControl;
import com.amay.tom.utils.env.EnvFile;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.utils.PrinterUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class Main extends Application {

    private final TicketsRepository ticketsRepository = TicketsRepository.getInstance();
    private IApplicationService applicationService ;


    @Override
    public void init() throws Exception {
        Logger.debug("Application init called");
        super.init();
        EnvFile.loadEnv();
//        SQLiteConnection.INSTANCE.setSQLiteConnection();
//        QRDataArray.readQRTicketFile();
//        try {
//            applicationService=new ApplicationService(new SystemControl());
//            new RedisListener(applicationService,RedisConnectionPool.getJedisPool()).startSCUEventListener(
//                    Channels.COMMAND_CHANNEL.name(), Channels.NOTIFICATION_CHANNEL.name());
//        } catch (Exception e) {
//            Logger.error("Error in starting RedisListener: {}", e);
//        }
//        ticketsRepository.testInsert(SQLiteConnection.INSTANCE.getConnection());
//        RedisSubscriberService redisSubscriberService = new RedisSubscriberService(RedisConnectionPool.getJedisPool());
//        redisSubscriberService.startListening("channel1", "channel2");



    }

    @Override
    public void stop() throws Exception {
        BNRIntegration.bnrClose();
        RedisConnectionPool.getJedisPool().close();
        DatabaseConnector.closeConnection();
        QRDataArray.createQRTicketFile();
        Logger.debug("Application stopped😒😒🙌");
        super.stop();
    }

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        applicationService=new ApplicationService(new SystemControl());



        //System.out.println("java version: "+System.getProperty("java.version"));
        //System.out.println("javafx.version: " + System.getProperty("javafx.version"));



        try {

            FXMLLoader fxmlLoader =new FXMLLoader(Main.class.getResource("initialize/tom-initialize-view.fxml"));
            fxmlLoader.setControllerFactory(param -> new TomInitializeViewController(applicationService));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

            // Add key event filter to prevent system keys
            scene.addEventFilter(KeyEvent.ANY, event -> {
                if (event.getCode() == KeyCode.WINDOWS ||
                        event.getCode() == KeyCode.COMMAND ||
                        event.getCode() == KeyCode.ALT ||
                        event.getCode() == KeyCode.TAB ||
                        event.getCode() == KeyCode.ESCAPE ||
                        (event.isAltDown() && event.getCode() == KeyCode.F4)) {
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
            stage.setMinHeight(768);
            stage.setMinWidth(1024);
            stage.setMaxHeight(768);
            stage.setMaxWidth(1024);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint(null);
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setTitle("TVM Application");
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
                Scene pduScene = new Scene(pduLoader.load(), 640, 448);

                Stage pduStage = new Stage();
                pduStage.setScene(pduScene);
                pduStage.setTitle("Amay PDU Monitor");
                pduStage.setX(screen2Bounds.getMinX());
                pduStage.setY(screen2Bounds.getMinY());
                pduStage.setFullScreen(true);
//            pduStage.setAlwaysOnTop(true);
                pduStage.setFullScreenExitHint(null);
                pduStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
                pduStage.initStyle(StageStyle.UNDECORATED);


                pduStage.show();

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

        }catch (Exception e){
            Logger.error("Error in loading main scene: {}", e);
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
//        EnvFile.loadEnv();
        launch();

    }

}