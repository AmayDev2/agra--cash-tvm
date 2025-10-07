package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.bnr.BNRIntegration;
import com.amay.tvm.bnr.BNRListener;
import com.amay.tvm.bnr.BNRListenerLoad;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.h2.util.Task;

public class MoneyManagementBnrLoadController{
    private final Agent agent;
    private final SceneManager sceneManager;

    @FXML private  Button cancel;
    @FXML private  Button rollback;
    @FXML private  Button commit;

    public MoneyManagementBnrLoadController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }


    public void onBnrLoad(ActionEvent actionEvent) {
        agent.getThreadPool().getFixedThreadPool().submit(new Task() {
            @Override
            public void call() throws Exception {BNRIntegration.bnrLoad(new BNRListenerLoad(MoneyManagementBnrLoadController.this,agent.getFinanceOperationRepository(),agent.getShift().getShiftId(),agent.getNoteAmountRepository()));}});
        resetButtons(true);
        actionEvent.consume();
    }

    public void onCommit(ActionEvent actionEvent) {
        BNRIntegration.bnrLoadCommit();
        actionEvent.consume();
    }

    public void onBackBnr(ActionEvent actionEvent) {
        sceneManager.back();
        actionEvent.consume();
    }

    public void onRollback(ActionEvent actionEvent) {
        BNRIntegration.bnrLoadRollback();
        actionEvent.consume();
    }

    public void onStop(ActionEvent actionEvent) {
        BNRIntegration.cancel();
        actionEvent.consume();

    }

    public void disableCancelButton() {
        resetButtons(false);
    }

    private void resetButtons(boolean stopAllowed){
        commit.setVisible(!stopAllowed);
        rollback.setVisible(!stopAllowed);
        cancel.setVisible(stopAllowed);
    }
}
