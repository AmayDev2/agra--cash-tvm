package com.amay.tom.pdu.controller;

import com.amay.printer.BNRLoadUnload;
import com.amay.printer.CoinLoadedReport;
import com.amay.printer.PrinterService;
import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.enums.FinanceOperation;
import com.amay.tvm.backend.mapper.FinanceOperationMapper;
import com.amay.tvm.controller.CoinRagistoryPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.network.monitorandcontrol.OperationMode;

import java.util.List;

public class MoneyManageController {
    private final Agent agent;
    private final SceneManager sceneManager;
    private final PrinterService printerService = new PrinterService();
    public MoneyManageController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;

    }

    @FXML
    private void onCoin(ActionEvent actionEvent) {

        FXMLLoader fxmlLoader= ViewFactory.getMoneyManagementCoin();
        CoinRagistoryPageController controller=new CoinRagistoryPageController(this.sceneManager,this.agent);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();

    }

    @FXML
    private  void onBnr(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader= ViewFactory.getMoneyManagementBnr();
        MoneyManagementBnrController controller=new MoneyManagementBnrController(this.agent,this.sceneManager);
        fxmlLoader.setControllerFactory((x)->controller);
        this.sceneManager.addToScene(fxmlLoader);
        actionEvent.consume();
    }

    @FXML
    private  void onBack(ActionEvent actionEvent) {
        this.sceneManager.back();
        actionEvent.consume();
    }

    @FXML
    private void onReports(ActionEvent actionEvent) {
        List<FinanceOperationEntity> financeOperationEntityCoinLoad = agent.getFinanceOperationRepository().getByShiftIdAndOperationType(agent.getShift().getShiftId(), FinanceOperation.COIN_LOAD.name());
        List<FinanceOperationEntity> financeOperationEntityCoinUnload = agent.getFinanceOperationRepository().getByShiftIdAndOperationType(agent.getShift().getShiftId(), FinanceOperation.COIN_UNLOAD.name());
        List<FinanceOperationEntity> financeOperationEntityBnrLoad = agent.getFinanceOperationRepository().getByShiftIdAndOperationType(agent.getShift().getShiftId(), FinanceOperation.BNR_LOAD.name());
        List<FinanceOperationEntity> financeOperationEntityBnrUnload = agent.getFinanceOperationRepository().getByShiftIdAndOperationType(agent.getShift().getShiftId(), FinanceOperation.BNR_UNLOAD.name());

        BNRLoadUnload bnrLoadReport = FinanceOperationMapper.toBNRLoadUnload(agent, FinanceOperation.BNR_LOAD, financeOperationEntityBnrLoad);
        BNRLoadUnload bnrUnlnoadReport = FinanceOperationMapper.toBNRLoadUnload(agent, FinanceOperation.BNR_UNLOAD, financeOperationEntityBnrUnload);
        CoinLoadedReport coinLoadReport = FinanceOperationMapper.toCoinLoadedReport(agent, FinanceOperation.COIN_LOAD, financeOperationEntityCoinLoad);
        CoinLoadedReport coinUnloadReport = FinanceOperationMapper.toCoinLoadedReport(agent, FinanceOperation.COIN_UNLOAD, financeOperationEntityCoinUnload);

        printerService.printBNRText(bnrLoadReport);
        printerService.printBNRText(bnrUnlnoadReport);
        printerService.printCoinLoadedReport(coinLoadReport);
        printerService.printCoinLoadedReport(coinUnloadReport);
    }
}
