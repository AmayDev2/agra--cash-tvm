package com.amay.tom.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TestToolController {

    // ================= TOP BAR =================
    @FXML private ComboBox<String> top_portCombo;
    @FXML private Button top_openPortBtn;
    @FXML private Button top_closePortBtn;
    @FXML private Button top_pollingStatusBtn;
    @FXML private Button top_getVersionBtn;
    @FXML private Button top_clearDisplayInfoBtn;
    @FXML private Button top_okBtn;

    // ================= MODULE TEST =================
    @FXML private ComboBox<String> module_dejammingMotorCombo;
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
    @FXML private CheckBox dejamming_h1Check;
    @FXML private CheckBox dejamming_h2Check;
    @FXML private CheckBox dejamming_h3Check;
    @FXML private Button dejamming_motorBtn;
    @FXML private Button dejamming_coinDumpBtn;

    // ================= AUTO POLLING =================
    @FXML private Button auto_startBtn;
    @FXML private Button auto_stopBtn;

    // ================= RETURN / ESCROW =================
    @FXML private ComboBox<String> return_escrowCombo;
    @FXML private ComboBox<String> return_coinReturnCombo;
    @FXML private Button diverter_ctrlBtn;

    // ================= LOG DELETE =================
    @FXML private TextField logs_daysField;
    @FXML private Button logs_controlBtn;

    // ================= LOG AREA =================
    @FXML private TextArea main_logArea;


    // ================= INITIALIZE =================
    @FXML
    public void initialize() {
        log("System Initialized.");

        // Fill combos with sample values
        top_portCombo.getItems().addAll("COM1", "COM2", "COM3", "COM4");
        module_dejammingMotorCombo.getItems().addAll("Left Motor", "Right Motor");
        return_escrowCombo.getItems().addAll("ESCROW 1", "ESCROW 2", "ESCROW 3");
        return_coinReturnCombo.getItems().addAll("RETURN 1", "RETURN 2", "RETURN 3");

        // TOP BAR handlers
        top_openPortBtn.setOnAction(e -> log("Opening Port: " + top_portCombo.getValue()));
        top_closePortBtn.setOnAction(e -> log("Closing Port"));
        top_pollingStatusBtn.setOnAction(e -> log("Polling Status requested"));
        top_getVersionBtn.setOnAction(e -> log("Getting Version..."));
        top_clearDisplayInfoBtn.setOnAction(e -> main_logArea.clear());
        top_okBtn.setOnAction(e -> log("OK button clicked"));

        // MODULE
        module_moduleTestBtn.setOnAction(e -> log("Module Test Triggered"));

        // BOX ID
        box_setBoxIdBtn.setOnAction(e -> log("Set Box ID: " + box_boxIdField.getText()));
        box_getCollectionBoxIdBtn.setOnAction(e -> log("Get Collection Box ID"));

        // SHUTTER
        shutter_closeRb.setOnAction(e -> log("Shutter Close"));
        shutter_openRb.setOnAction(e -> log("Shutter Open"));

        // REGISTER
        reg_notClearRb.setOnAction(e -> log("Register Not Clear"));
        reg_clearRb.setOnAction(e -> log("Register Clear"));

        // DENOMINATIONS
        rs1_activateRb.setOnAction(e -> log("Rs1 Activated"));
        rs1_inhibitRb.setOnAction(e -> log("Rs1 Inhibited"));

        rs2_activateRb.setOnAction(e -> log("Rs2 Activated"));
        rs2_inhibitRb.setOnAction(e -> log("Rs2 Inhibited"));

        rs5_activateRb.setOnAction(e -> log("Rs5 Activated"));
        rs5_inhibitRb.setOnAction(e -> log("Rs5 Inhibited"));

        rs10_activateRb.setOnAction(e -> log("Rs10 Activated"));
        rs10_inhibitRb.setOnAction(e -> log("Rs10 Inhibited"));

        rs20_activateRb.setOnAction(e -> log("Rs20 Activated"));
        rs20_inhibitRb.setOnAction(e -> log("Rs20 Inhibited"));

        coin_acceptPollingBtn.setOnAction(e -> log("Manual Coin Acceptance Polling"));

        // HOPPERS
        hopper_coinChangeBtn.setOnAction(e -> log("Coin Change triggered"));

        // DEJAMMING
        dejamming_motorBtn.setOnAction(e -> log("Dejamming Motor cycles: " + dejamming_cyclesField.getText()));
        dejamming_coinDumpBtn.setOnAction(e -> log("Coin Dump"));

        // AUTO POLLING
        auto_startBtn.setOnAction(e -> log("Auto Polling Started"));
        auto_stopBtn.setOnAction(e -> log("Auto Polling Stopped"));

        // RETURN / DIVERTER
        diverter_ctrlBtn.setOnAction(e -> log("Diverter Control Triggered"));

        // LOG DELETE
        logs_controlBtn.setOnAction(e -> log("Deleting logs older than " + logs_daysField.getText() + " days"));
    }

    // ================= LOGGER =================
    private void log(String msg) {
        main_logArea.appendText(msg + "\n");
    }
}
