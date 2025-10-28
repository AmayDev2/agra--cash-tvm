package com.amay.tvm.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.controller.components.StatusBottomBarView;
import com.amay.tom.model.station.Station;
import com.amay.tom.model.TicketType;
import com.amay.tom.repository.FareLine3;
import com.amay.tom.repository.StationData;
import com.amay.tvm.util.Snackbar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.Arrays;
import java.util.List;

//NEW STATION SELECTION
public class TicketSelectionController {
    @FXML
    private GridPane stationGrid;

    private Station currentStation = null;

    private Station selectedDestination;

    private BorderPane borderPane;
    private StackPane pane;
    private StationData stationData;
    private Agent agent;
    private TicketType ticketType;



    public TicketSelectionController(BorderPane borderPane, StackPane stackPane, Agent agent, StationData stationData, TicketType ticketType) {
        this.borderPane = borderPane;
        this.pane = stackPane;
        this.agent = agent;
        this.stationData = stationData;
        this.ticketType=ticketType;
        currentStation = SystemConfig.getInstance().getCurrentStation();

    }


    @FXML
    private void initialize() {
        populateStations(Arrays.stream(stationData.getStationArray()).toList());
    }



    public void populateStations(List<Station> stations) {
        selectedDestination = null;
        stationGrid.getChildren().clear();
        stationGrid.setHgap(20);
        stationGrid.setVgap(10);
    int columns = 4;
    int row = 0, col = 0;

    ToggleGroup group = new ToggleGroup();

        for (
    Station station : stations) {
        ToggleButton btn = new ToggleButton(station.getStationName());
        btn.setMinWidth(220);
        btn.setMinHeight(45);
        btn.setId(station.getStationId());
        btn.setUserData(station);
        btn.setToggleGroup(group);
        btn.getStyleClass().add("station-btn");

        if (station.getStationId().equals(currentStation.getStationId())) {
            btn.setDisable(true);
            btn.getStyleClass().add("disabled-station");
        }

        // ✅ Use MousePressed instead of setOnAction
        btn.setOnMousePressed(e -> {
            if (btn.equals(group.getSelectedToggle())) {
                // Deselect
                group.selectToggle(null);
                selectedDestination = null;
                e.consume(); // Prevent default selection behavior
            }
        });

        // ✅ Set destination on action (after actual toggle happens)
        btn.setOnAction(e -> {
            if (btn.isSelected()) {
                selectedDestination = (Station) btn.getUserData();
            }
        });

        stationGrid.add(btn, col, row);

        col++;
        if (col == columns) {
            col = 0;
            row++;
        }

        btn.getStyleClass().add("main-button");

        }
}


    @FXML
    private void navigateToHomePage(ActionEvent actionEvent) {
        int count = pane.getChildren().size();
//        this.addBottomBarView();
        if (count > 0) {
            pane.getChildren().remove(count - 1); // remove top-most child
        }
        actionEvent.consume();

    }

    private void addBottomBarView() {
        try {
            FXMLLoader fxmlLoader = ViewFactory.getBottomNav();
            fxmlLoader.setControllerFactory(x -> new StatusBottomBarView(agent.getPeripheralMonitor(), agent.getVersions(),agent.getMasterConfigInfo()));
//            borderPane.setBottom(fxmlLoader.load());
        } catch (RuntimeException e) {
            System.err.println("Error loading bottom navigation view: " + e.getMessage());
        }
    }

    @FXML
    private void  confirmSelection(ActionEvent actionEvent) {
        if (selectedDestination == null) {
            Snackbar.INSTANCE.showSnackbar(pane,"Please Select Destination Station",true,0);
            return;
        }
        try {
//            FXMLLoader loader = ViewFactory.getTicketSelectionDetailsView();
//            loader.setControllerFactory(c -> new StationSelectionController(this.borderPane,this.pane, agent, stationData, ticketType,selectedDestination));
            FXMLLoader fxmlLoader = ViewFactory.getPaymentSummeryView();
            int srcId = Integer.parseInt(this.currentStation.getStationId());
            int desId = Integer.parseInt(this.selectedDestination.getStationId());
            int multiplier = this.ticketType.equals(TicketType.RETURN) ? 2 : 1;
            int fare = FareLine3.distanceMatrix[srcId-1][desId-1]*multiplier;
            fare*=multiplier;
            fare=(int)(fare*agent.getBusinessRule().getFareMultiplayer());
            int finalFare = fare;
            fxmlLoader.setControllerFactory(param -> new PaymentController(
                    this.pane, this.borderPane, this.agent, this.stationData,
                    this.selectedDestination, this.ticketType , finalFare
            ));
            this.pane.getChildren().add(fxmlLoader.load());
        }catch (Exception e) {
            e.printStackTrace();
        }
        actionEvent.consume();
    }
}
