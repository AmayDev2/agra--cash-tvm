package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.backend.enums.LoggerTag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.tinylog.Logger;

public class ReportController {
    private final Agent agent;
    private final SceneManager sceneManager;
    @FXML private Button eos;

    public ReportController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }

    @FXML
    void initialize() {
        try {
            Logger.tag(LoggerTag.APP).info(agent.getShift().getShiftId());
        }catch (Exception e){
            eos.setDisable(true);
        }

    }


    @FXML private void onBack(ActionEvent actionEvent) {

        sceneManager.back();
        actionEvent.consume();
    }

    @FXML private void onEOS(ActionEvent actionEvent) {
        agent.getInternalListener().EOShift();
        actionEvent.consume();
    }
}
