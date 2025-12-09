package com.amay.tom.pdu.controller;

import com.amay.printer.BalanceReport;
import com.amay.printer.PrinterCommandDispatcher;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.session.ShiftDto;
import com.amay.tom.model.siftdata.ShiftManagementDTO;
import com.amay.tom.model.siftdata.ShiftManagementMapper;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.service.siftservice.ShiftService;
import com.amay.tom.service.siftservice.impl.ShiftServiceImpl;
import com.amay.tom.utils.time.TimeUtil;
import com.amay.tvm.backend.dto.NoteAmountDTO;
import com.amay.tvm.backend.enums.LoggerTag;
import com.amay.tvm.backend.mapper.NoteAmountMapper;
import com.amay.tvm.coin.CoinModuleInterface;
import com.amay.tvm.coin.service.HoppersRegistry;
import com.amay.tvm.util.Page.FocusUtil;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tinylog.Logger;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportController {
    private final Agent agent;
    private final SceneManager sceneManager;
    private final ToggleGroup toggleGroup = new ToggleGroup();
    @FXML private Button backbutton;
    ShiftManagementDTO selectedItem;// shared for all rows
    ShiftService shiftService;
    @FXML public TableView<ShiftManagementDTO> reportTable;
    @FXML public TableColumn<ShiftManagementDTO, String> shiftIdColumn;
    @FXML public TableColumn<ShiftManagementDTO, String> startTimeColumn;
    @FXML public TableColumn<ShiftManagementDTO, String> endTimeColumn;
    @FXML public TableColumn<ShiftManagementDTO, String> operatorIdColumn;
    @FXML public TableColumn<ShiftManagementDTO, Boolean> selectColumn;
    @FXML private Button printButton;
    @FXML private Button eos;
    @FXML private Button balanceQuery;




    private static int N = 10;

    public ReportController(Agent agent, SceneManager sceneManager) {
        this.agent = agent;
        this.sceneManager = sceneManager;
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> printButton.requestFocus());
        FocusUtil.configureTabOrder(
                printButton,
                eos,
                balanceQuery,
                backbutton
        );

        ;
        try {
            Logger.tag(LoggerTag.APP).info(agent.getShift().getShiftId());


        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ---- Radio button column ----
        selectColumn.setCellFactory(col -> new TableCell<ShiftManagementDTO, Boolean>() {
            private final RadioButton radioButton = new RadioButton();

            {
                setAlignment(javafx.geometry.Pos.CENTER);
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setOnAction(event -> {
                    selectedItem = getTableView().getItems().get(getIndex());
                    // Unselect all other rows
                    for (ShiftManagementDTO row : getTableView().getItems()) {
                        row.getSelected().set(false);
                    }
                    selectedItem.getSelected().set(true);
                });
            }

            @Override
            protected void updateItem(Boolean selected, boolean empty) {
                super.updateItem(selected, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    radioButton.setSelected(getTableView().getItems().get(getIndex()).getSelected().get());
                    setGraphic(radioButton);
                }
            }
        });

        shiftIdColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getShiftId()));
        operatorIdColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getOperatorId()));
        startTimeColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getStartTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
        endTimeColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getEndTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
        addLastNShifts(N);
        shiftService=agent.getShiftService();
        FocusUtil.configureFocus(reportTable,backbutton);
        }catch (Exception e){
            eos.setDisable(true);
        }
    }

    private void addLastNShifts(int n) {
        List<ShiftManagementDTO> shiftManagementDTOs = getLastNShiftDetails(n);
        reportTable.getItems().setAll(shiftManagementDTOs); // replace items
    }

    private List<ShiftManagementDTO> getLastNShiftDetails(int n) {
        List<ShiftDto> shiftDtos = agent.getShiftRepository().findLastNShifts(n);
        List<ShiftManagementDTO> shiftManagementDTOs = new ArrayList<>();
        for (ShiftDto shiftDto : shiftDtos) {
            shiftManagementDTOs.add(ShiftManagementMapper.fromDto(shiftDto));
        }
        return shiftManagementDTOs;
    }

    public void onClickPrint(ActionEvent actionEvent) {
        if(selectedItem!=null){
            String shiftId = selectedItem.getShiftId();
            shiftService.printShiftReport(shiftId);
        }
        actionEvent.consume();
    }

    @FXML private void onBack(ActionEvent actionEvent) {
        sceneManager.back();
        actionEvent.consume();
    }

    @FXML private void onEOS(ActionEvent actionEvent) {
        agent.getInternalListener().EOShift();
        actionEvent.consume();
    }

    public void onBalance(ActionEvent actionEvent) {
        NoteAmountDTO rs10=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(10));
        NoteAmountDTO rs20=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(20));
        NoteAmountDTO rs50=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(50));
        NoteAmountDTO rs100=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(100));
        NoteAmountDTO rs200=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(200));
        NoteAmountDTO rs500=NoteAmountMapper.toDto(agent.getNoteAmountRepository().findById(500));

        int totalQuantity= rs10.getCashInQuantity()+rs20.getCashInQuantity()+rs50.getCashInQuantity()
                +rs100.getCashInQuantity()+rs200.getCashInQuantity()+rs500.getCashInQuantity();

        int totalAmount= rs10.getCashInQuantity()*10+rs20.getCashInQuantity()*20+rs50.getCashInQuantity()*50
                +rs100.getCashInQuantity()*100+rs200.getCashInQuantity()*200+rs500.getCashInQuantity()*500;

        PrinterCommandDispatcher.INSTANCE.printBalanceReport(
                BalanceReport.builder()
                        .reportType("Balance Report")
                        .stationName(SystemConfig.getInstance().getCurrentStation().getStationName())
                        .shiftId(agent.getShiftMaintenance().getShiftId())
                        .startTime(TimeUtil.formated(agent.getShiftMaintenance().getStartTime()))
                        .endTime("-")
                        .equipmentId(SystemConfig.getInstance().getCurrentEquipment().getEquipmentId())
                        .operatorId(agent.getShiftMaintenance().getOperatorId())
                        .rs10Count(rs10.getCashInQuantity())
                        .rs10Amount(rs10.getCashInQuantity()*10)
                        .rs20Count(rs20.getCashInQuantity())
                        .rs20Amount(rs20.getCashInQuantity()*20)
                        .rs50Count(rs50.getCashInQuantity())
                        .rs50Amount(rs50.getCashInQuantity()*50)
                        .rs100Count(rs100.getCashInQuantity())
                        .rs100Amount(rs100.getCashInQuantity()*100)
                        .rs200Count(rs200.getCashInQuantity())
                        .rs200Amount(rs200.getCashInQuantity()*200)
                        .rs500Count(rs500.getCashInQuantity())
                        .rs500Amount(rs500.getCashInQuantity()*500)
                        .bankTotalCount(totalQuantity)
                        .bankTotalAmount(totalAmount)
                        .hopper1Count(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("1")))
                        .hopper2Count(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("2")))
                        .hopper3Count(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("3")))
                        .hopper1Amount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("1"))*5)
                        .hopper2Amount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("2"))*10)
                        .hopper3Amount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("3"))*10)
                        .coinTotalCount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("1"))+
                                Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("2"))+
                                Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("3")))
                        .coinTotalAmount(Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("1"))*5+
                                Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("2"))*10+
                                Integer.parseInt(HoppersRegistry.INSTANCE.getHopperQuantity("3"))*10)
                        .build()
                );



        actionEvent.consume();
    }
}
