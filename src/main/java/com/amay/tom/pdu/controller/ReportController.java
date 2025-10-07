package com.amay.tom.pdu.controller;

import com.amay.tom.agent.Agent;
import com.amay.tom.model.session.ShiftDto;
import com.amay.tom.model.siftdata.ShiftManagementDTO;
import com.amay.tom.model.siftdata.ShiftManagementMapper;
import com.amay.tom.pdu.controller.service.SceneManager;
import com.amay.tom.service.siftservice.ShiftService;
import com.amay.tom.service.siftservice.impl.ShiftServiceImpl;
import com.amay.tvm.backend.enums.LoggerTag;
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
    ShiftManagementDTO selectedItem;// shared for all rows
    ShiftService shiftService;
    @FXML public TableView<ShiftManagementDTO> reportTable;
    @FXML public TableColumn<ShiftManagementDTO, String> shiftIdColumn;
    @FXML public TableColumn<ShiftManagementDTO, String> startTimeColumn;
    @FXML public TableColumn<ShiftManagementDTO, String> endTimeColumn;
    @FXML public TableColumn<ShiftManagementDTO, String> operatorIdColumn;
    @FXML public TableColumn<ShiftManagementDTO, Boolean> selectColumn;
    @FXML private Button eos;
    private static int N = 10;

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
            shiftService.printEOShift(shiftId);
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
}
