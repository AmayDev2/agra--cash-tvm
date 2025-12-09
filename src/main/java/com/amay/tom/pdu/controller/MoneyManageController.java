package com.amay.tom.pdu.controller;

import com.amay.printer.BNRLoadUnload;
import com.amay.printer.CoinLoadedReport;
import com.amay.printer.PrinterCommandDispatcher;
import com.amay.printer.PrinterService;
import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.model.session.Shift;
import com.amay.tom.model.session.ShiftDto;
import com.amay.tom.model.session.ShiftMapper;
import com.amay.tom.model.user.entity.User;
import com.amay.tom.model.user.entity.UserPrivilege;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tvm.backend.entity.FinanceOperationEntity;
import com.amay.tvm.backend.enums.FinanceOperation;
import com.amay.tvm.backend.mapper.FinanceOperationMapper;
import com.amay.tvm.controller.CoinRagistoryPageController;
import com.amay.tvm.util.Page.FocusUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.network.monitorandcontrol.OperationMode;
import java.util.List;
import java.util.Optional;

public class MoneyManageController {
    private final Agent agent;
    private final SceneManager sceneManager;
    @FXML
    private GridPane root;
    @FXML
    private Button coinButton;
    @FXML
    private Button bnrButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button backButton;

    public MoneyManageController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }
    @FXML
    private void initialize(){
//        UserPrivilege privilege=agent.getUserPrivilege();
//        coinButton.setDisable(!(privilege.isCoinRefill() || privilege.isCoinDumping()));
//        bnrButton.setDisable(!privilege.isBnrCashAdd());           // If no BNR access → disable
//        reportsButton.setDisable(!privilege.isImportAndExport());
        FocusUtil.configureFocus(coinButton,backButton);
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
        ShiftDto shiftDto = agent.getShiftRepository()
                .findById(agent.getShiftMaintenance().getShiftId())
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        Shift shift=ShiftMapper.toModel(shiftDto);

        List<FinanceOperationEntity> financeOperationEntityCoinLoad = agent.getFinanceOperationRepository().getByShiftIdAndOperationType(shift.getShiftId(), FinanceOperation.COIN_LOAD.name());
        List<FinanceOperationEntity> financeOperationEntityCoinUnload = agent.getFinanceOperationRepository().getByShiftIdAndOperationType(shift.getShiftId(), FinanceOperation.COIN_UNLOAD.name());
        List<FinanceOperationEntity> financeOperationEntityBnrLoad = agent.getFinanceOperationRepository().getByShiftIdAndOperationType(shift.getShiftId(), FinanceOperation.BNR_LOAD.name());
        List<FinanceOperationEntity> financeOperationEntityBnrUnload = agent.getFinanceOperationRepository().getByShiftIdAndOperationType(shift.getShiftId(), FinanceOperation.BNR_UNLOAD.name());

        BNRLoadUnload bnrLoadReport = FinanceOperationMapper.toBNRLoadUnload(shift, FinanceOperation.BNR_LOAD, financeOperationEntityBnrLoad);
        BNRLoadUnload bnrUnloadReport = FinanceOperationMapper.toBNRLoadUnload(shift, FinanceOperation.BNR_UNLOAD, financeOperationEntityBnrUnload);
        CoinLoadedReport coinLoadReport = FinanceOperationMapper.toCoinLoadedReport(shift, FinanceOperation.COIN_LOAD, financeOperationEntityCoinLoad);
        CoinLoadedReport coinUnloadReport = FinanceOperationMapper.toCoinLoadedReport(shift, FinanceOperation.COIN_UNLOAD, financeOperationEntityCoinUnload);

        if(bnrLoadReport!=null)PrinterCommandDispatcher.INSTANCE.printBNRLoadUnload(bnrLoadReport);
        if(bnrUnloadReport!=null)PrinterCommandDispatcher.INSTANCE.printBNRLoadUnload(bnrUnloadReport);
        if(coinLoadReport!=null)PrinterCommandDispatcher.INSTANCE.printCoinLoadedReport(coinLoadReport);
        if(coinUnloadReport!=null)PrinterCommandDispatcher.INSTANCE.printCoinLoadedReport(coinUnloadReport);

        actionEvent.consume();
    }
}
