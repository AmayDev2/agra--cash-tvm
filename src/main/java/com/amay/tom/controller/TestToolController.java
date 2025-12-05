package com.amay.tom.controller;

import com.amay.tom.coin.CoinModuleInterface;
import com.amay.tom.coin.enums.Diverter;
import com.amay.tom.coin.enums.Escrow;
import com.amay.tom.coin.enums.ModuleTestCode;
import com.amay.tom.coin.enums.Range;
import com.amay.tom.coin.model.CoinPollRequest;
import com.amay.tom.coin.util.OverHeadDisplay;
import com.amay.tom.config.DataTransfer;
import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestToolController {


    @FXML private Button clear;
    @FXML private Button sendBMP;
    @FXML private Button connection;
    @FXML private TextField commands;
    @FXML private Button applyCommand;
    @FXML private ComboBox top_baudRate;
    @FXML private Button quick_resetBtn;
    @FXML private Button quick_testAllBtn;
    // ================= TOP BAR =================
    @FXML private ComboBox<String> top_portCombo;
    @FXML private Button top_openPortBtn;
    @FXML private Button top_closePortBtn;
    @FXML private Button top_pollingStatusBtn;
    @FXML private Button top_getVersionBtn, top_getModuleResetBtn;
    @FXML private Button top_clearDisplayInfoBtn;
    @FXML private Button top_okBtn;

    // ================= MODULE TEST =================
    @FXML private ComboBox<ModuleTestCode> module_dejammingMotorCombo;
    @FXML private Button module_moduleTestBtn;

    // ================= BOX ID =================
    @FXML private Button box_setBoxIdBtn;
    @FXML private Button box_getCollectionBoxIdBtn;
    @FXML private TextField box_boxIdField;

    // ================= SHUTTER + REGISTERS =================
    @FXML private ToggleGroup tg_shutter;
    @FXML private RadioButton shutter_closeRb;
    @FXML private RadioButton shutter_openRb;

    @FXML private ToggleGroup tg_register;
    @FXML private RadioButton reg_notClearRb;
    @FXML private RadioButton reg_clearRb;

    // ================= DENOMINATION GROUPS =================
    @FXML private ToggleGroup tg_rs1;
    @FXML private RadioButton rs1_activateRb;
    @FXML private RadioButton rs1_inhibitRb;

    @FXML private ToggleGroup tg_rs2;
    @FXML private RadioButton rs2_activateRb;
    @FXML private RadioButton rs2_inhibitRb;

    @FXML private ToggleGroup tg_rs5;
    @FXML private RadioButton rs5_activateRb;
    @FXML private RadioButton rs5_inhibitRb;

    @FXML private ToggleGroup tg_rs10;
    @FXML private RadioButton rs10_activateRb;
    @FXML private RadioButton rs10_inhibitRb;

    @FXML private ToggleGroup tg_rs20;
    @FXML private RadioButton rs20_activateRb;
    @FXML private RadioButton rs20_inhibitRb;

    @FXML private Button coin_acceptPollingBtn;

    // ================= HOPPER =================
    @FXML private TextField hopper_h1Field;
    @FXML private TextField hopper_h2Field;
    @FXML private TextField hopper_h3Field;
    @FXML private Button hopper_coinChangeBtn;

    // ================= DEJAMMING =================
    @FXML private TextField dejamming_cyclesField;
    @FXML private Button dejamming_h1Check;
    @FXML private Button dejamming_h2Check;
    @FXML private Button dejamming_h3Check;
    @FXML private Button dejamming_h4Check;
    @FXML private Button dejamming_h5Check;
    @FXML private Button dejamming_h6Check;
    @FXML private Button dejamming_motorBtn;
//    @FXML private Button dejamming_coinDumpBtn;

    // ================= AUTO POLLING =================
    @FXML private Button auto_startBtn;
    @FXML private Button auto_stopBtn;

    // ================= RETURN / ESCROW =================
    @FXML private ComboBox<Escrow> return_escrowCombo;
    @FXML private Button escrow_ctrlBtn;
    @FXML private ComboBox<Diverter> return_coinReturnCombo;
    @FXML private Button diverter_ctrlBtn;


    // ================= LOG DELETE =================
    @FXML private TextField logs_daysField;
    @FXML private Button logs_controlBtn;

    // ================= LOG AREA =================
    @FXML private TextArea main_logArea;

    private boolean isLightOn=true;
    private boolean isBuzzerOn=true;

    private byte[] hexStringToBytes(String hex) {
        hex = hex.trim();

        // Must be even length
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string length");
        }

        int length = hex.length() / 2;
        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {
            String byteStr = hex.substring(i * 2, i * 2 + 2);
            result[i] = (byte) Integer.parseInt(byteStr, 16);
        }

        return result;
    }




    // ================= INITIALIZE =================
    @FXML
    public void initialize() {
        initialize1();
    }
    private void initialize1() {
        log("System Initialized.");
        SerialPort[] portList = SerialPort.getCommPorts();
        top_baudRate.getItems().addAll(
                "110", "300", "600", "1200", "2400", "4800", "9600",
                "14400", "19200", "38400", "57600", "115200", "128000", "256000"
        );

        applyCommand.setOnAction(e -> {
            try {
                byte[] data = hexStringToBytes(commands.getText());
                CoinModuleInterface.INSTANCE.applyRowCommand(data);
            } catch (Exception ex) {
                log(ex.getMessage());
            }
        });
        // Clear previous values (optional)
        top_portCombo.getItems().clear();

        for (SerialPort port : portList) {
            String portName = port.getSystemPortName();      // Example: COM3, COM4, etc.
            // String portName = port.getDescriptivePortName(); // If you prefer descriptive names
            top_portCombo.getItems().add(portName);
        }

        if (!top_portCombo.getItems().isEmpty()) {
            top_portCombo.getSelectionModel().selectFirst(); // Auto-select first available port
        }

        if(!top_baudRate.getItems().isEmpty()){
            top_baudRate.getSelectionModel().selectFirst();
        }

        module_dejammingMotorCombo.getItems().addAll(ModuleTestCode.values());
        return_escrowCombo.getItems().addAll(Escrow.values());
        return_coinReturnCombo.getItems().addAll(Diverter.values());

        quick_testAllBtn.setOnAction(e->{
            try {
                isLightOn=!isLightOn;
               CoinModuleInterface.INSTANCE.controlLightTray(isLightOn);
            } catch (Exception ex) {
                log(ex.getMessage());
            }
        });

        quick_resetBtn.setOnAction(e->{
            try {
                isBuzzerOn=!isBuzzerOn;
               CoinModuleInterface.INSTANCE.controlAlarmTray(isBuzzerOn);
            } catch (Exception ex) {
                log(ex.getMessage());
            }
        });

        DataTransfer listener= this::log;






        // RETURN / DIVERTER
        diverter_ctrlBtn.setVisible(false);
//        diverter_ctrlBtn.setOnAction(e -> {
//            try {
//                log(CoinModuleInterface.INSTANCE.controlDivert(return_coinReturnCombo.getSelectionModel().getSelectedItem()).toString());
//            } catch (Exception ex) {
//                log(ex.getMessage());
//            }
//        });
        escrow_ctrlBtn.setOnAction(e-> {
            try {
                log(CoinModuleInterface.INSTANCE.controlEscrow( return_escrowCombo.getSelectionModel().getSelectedItem(),return_coinReturnCombo.getSelectionModel().getSelectedItem()).toString());
            } catch (Exception ex) {
                log(ex.getMessage());
            }
        });

        // TOP BAR handlers
        top_openPortBtn.setOnAction(e -> {
            log("Opening @ "+ top_baudRate.getSelectionModel().getSelectedItem()+" "+top_portCombo.getValue());
            CoinModuleInterface.INSTANCE.setupCoinModule(top_portCombo.getValue(), (String) top_baudRate.getSelectionModel().getSelectedItem());
            CoinModuleInterface.INSTANCE.setlistener( listener);
            log("Opened");
        });

        top_closePortBtn.setOnAction(e -> log("Closing Port "+CoinModuleInterface.INSTANCE.closeCoinModule()));
        top_pollingStatusBtn.setOnAction(e ->log(CoinModuleInterface.INSTANCE.pooling().toString()));
        top_getVersionBtn.setOnAction(e ->
                {try{log(CoinModuleInterface.INSTANCE.getVersion().toString());
        } catch (Exception ex) {
                    log(ex.getMessage());
        }}
        );
        top_getModuleResetBtn.setOnAction(e ->
                {try{log(CoinModuleInterface.INSTANCE.getModuleReset().toString());
                } catch (Exception ex) {
                    log(ex.getMessage());
                }}
        );
        top_clearDisplayInfoBtn.setOnAction(e -> main_logArea.clear());
        top_okBtn.setOnAction(e -> Platform.exit());

        // MODULE
        module_moduleTestBtn.setOnAction(e -> log(CoinModuleInterface.INSTANCE.testModule(module_dejammingMotorCombo.getSelectionModel().getSelectedItem()).toString()));

        // BOX ID
        box_setBoxIdBtn.setOnAction(e -> log(CoinModuleInterface.INSTANCE.setBoxId(Byte.parseByte(box_boxIdField.getText())).toString()));
        box_getCollectionBoxIdBtn.setOnAction(e ->log(CoinModuleInterface.INSTANCE.getBoxId().toString()));



// State holders
        AtomicBoolean isShutterOpen   = new AtomicBoolean(false);
        AtomicBoolean clearRegister   = new AtomicBoolean(false);

        AtomicBoolean inhibitRs1  = new AtomicBoolean(false);
        AtomicBoolean inhibitRs2  = new AtomicBoolean(false);
        AtomicBoolean inhibitRs5  = new AtomicBoolean(false);
        AtomicBoolean inhibitRs10 = new AtomicBoolean(false);
        AtomicBoolean inhibitRs20 = new AtomicBoolean(false);

// SHUTTER
        shutter_closeRb.setOnAction(e -> {
            isShutterOpen.set(false);
            log("Shutter Closed");
        });
        shutter_openRb.setOnAction(e -> {
            isShutterOpen.set(true);
            log("Shutter Open");
        });

// REGISTER
        reg_notClearRb.setOnAction(e -> {
            clearRegister.set(false);
            log("Register Not Clear");
        });
        reg_clearRb.setOnAction(e -> {
            clearRegister.set(true);
            log("Register Clear");
        });

// DENOMINATIONS
        rs1_activateRb.setOnAction(e -> {
            inhibitRs1.set(false);
            log("Rs1 Activated");
        });
        rs1_inhibitRb.setOnAction(e -> {
            inhibitRs1.set(true);
            log("Rs1 Inhibited");
        });

        rs2_activateRb.setOnAction(e -> {
            inhibitRs2.set(false);
            log("Rs2 Activated");
        });
        rs2_inhibitRb.setOnAction(e -> {
            inhibitRs2.set(true);
            log("Rs2 Inhibited");
        });

        rs5_activateRb.setOnAction(e -> {
            inhibitRs5.set(false);
            log("Rs5 Activated");
        });
        rs5_inhibitRb.setOnAction(e -> {
            inhibitRs5.set(true);
            log("Rs5 Inhibited");
        });

        rs10_activateRb.setOnAction(e -> {
            inhibitRs10.set(false);
            log("Rs10 Activated");
        });
        rs10_inhibitRb.setOnAction(e -> {
            inhibitRs10.set(true);
            log("Rs10 Inhibited");
        });

        rs20_activateRb.setOnAction(e -> {
            inhibitRs20.set(false);
            log("Rs20 Activated");
        });
        rs20_inhibitRb.setOnAction(e -> {
            inhibitRs20.set(true);
            log("Rs20 Inhibited");
        });

// POLL BUTTON ACTION
        coin_acceptPollingBtn.setOnAction(e -> {
            CoinPollRequest req = new CoinPollRequest(
                    isShutterOpen.get(),   // Bit7
                    clearRegister.get(),   // Bit6
                    inhibitRs20.get(),     // Bit4
                    inhibitRs10.get(),     // Bit3
                    inhibitRs5.get(),      // Bit2
                    inhibitRs2.get(),      // Bit1
                    inhibitRs1.get()       // Bit0
            );

            try {
                log("Manual Coin Polling Response: " + CoinModuleInterface.INSTANCE.poolingAcceptance(req).parse().toString());
            } catch (Exception ex) {
                log("Manual Coin Polling Response: "+ex.getMessage());
            }
        });

        // HOPPERS
        hopper_coinChangeBtn.setOnAction(e ->{
            new Thread(()->{
                if(hopper_h1Field.getText()!= null && !hopper_h1Field.getText().isBlank() && Integer.parseInt(hopper_h1Field.getText())>0)log(CoinModuleInterface.INSTANCE.coinChange(1, Integer.parseInt(hopper_h1Field.getText())).toString());
                if(hopper_h2Field.getText()!= null && !hopper_h2Field.getText().isBlank() && Integer.parseInt(hopper_h2Field.getText())>0)log(CoinModuleInterface.INSTANCE.coinChange(2, Integer.parseInt(hopper_h2Field.getText())).toString());
                if(hopper_h3Field.getText()!= null && !hopper_h3Field.getText().isBlank() && Integer.parseInt(hopper_h3Field.getText())>0)log(CoinModuleInterface.INSTANCE.coinChange(3, Integer.parseInt(hopper_h3Field.getText())).toString());
            }).start();
        }
        );

        // DE_JAMMING
        dejamming_motorBtn.setOnAction(e -> log(CoinModuleInterface.INSTANCE.deJamming(Range.ONE.get(Integer.parseInt(dejamming_cyclesField.getText()))).toString()));
        dejamming_h1Check.setOnAction(e -> {
            new Thread(()->{
               log(Objects.requireNonNull(CoinModuleInterface.INSTANCE.dumpHopper(1)).toString());
            }).start();
        });
        dejamming_h2Check.setOnAction(e -> {
            new Thread(()->{
                log(Objects.requireNonNull(CoinModuleInterface.INSTANCE.dumpHopper(2)).toString());
            }).start();
        });
        dejamming_h3Check.setOnAction(e -> {
            new Thread(()->{
                log(Objects.requireNonNull(CoinModuleInterface.INSTANCE.dumpHopper(3)).toString());
            }).start();
        });
        dejamming_h4Check.setOnAction(e -> {
            new Thread(()->{
                log(Objects.requireNonNull(CoinModuleInterface.INSTANCE.dumpHopper(4)).toString());
            }).start();
        });
        dejamming_h5Check.setOnAction(e -> {
            new Thread(()->{
                log(Objects.requireNonNull(CoinModuleInterface.INSTANCE.dumpHopper(5)).toString());
            }).start();
        });
        dejamming_h6Check.setOnAction(e -> {
            new Thread(()->{
                log(Objects.requireNonNull(CoinModuleInterface.INSTANCE.dumpHopper(6)).toString());
            }).start();
        });

        // AUTO POLLING
        auto_startBtn.setOnAction(e -> startAutoPolling());
        auto_stopBtn.setOnAction(e -> stopAutoPolling());


        // LOG DELETE
        logs_controlBtn.setOnAction(e -> log("Deleting logs older than " + logs_daysField.getText() + " days"));

        clear.setOnAction(e->{
            byte[][] data = OverHeadDisplay.getClear();
            for(var x:data)
            new Thread(()->{
                try {
                    CoinModuleInterface.INSTANCE.applyRowCommand(x);
                } catch (Exception ex) {
                    log(ex.getMessage());
                }
            }).start();

        });

        sendBMP.setOnAction(e->{
            byte[][] data = OverHeadDisplay.getSendBMPs();
            for(var x:data)
                try {
                    CoinModuleInterface.INSTANCE.applyRowCommand(x);
                } catch (Exception ex) {
                    log(ex.getMessage());
                }
        });
        connection.setOnAction(e->{
            try {
                byte[] data = OverHeadDisplay.getConnection();
               CoinModuleInterface.INSTANCE.applyRowCommand(data);
            } catch (Exception ex) {
                log(ex.getMessage());
            }
        });
    }

    // ================= LOGGER =================
    private void log(String msg) {
        Platform.runLater(()->main_logArea.appendText(msg + "\n"));
    }


    private void startAutoPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            log("Auto Polling already running...");
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            // Your polling logic here
            Platform.runLater(() -> log(CoinModuleInterface.INSTANCE.pooling().toString()));
        }, 0, 5, TimeUnit.SECONDS); // run every 5 seconds

        log("Auto Polling Started");
    }
    private ScheduledExecutorService scheduler;

    private void stopAutoPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            log("Auto Polling Stopped");
        } else {
            log("Auto Polling already stopped");
        }
    }

}
