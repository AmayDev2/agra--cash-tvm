package com.amay.tom.controller;


import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.service.cancel.ICancelService;
import com.amay.tom.service.cancel.impl.CancelService;
import com.amay.tom.utils.time.TimeUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class CancelViewController {

    @FXML
    private Button clearButton;
    //scu,ccu,local
    @FXML
    private GridPane gridView;

    private ICancelService cancelService;

    private Agent agent;

//    private Connection sqLiteConnection = null;


    public CancelViewController(Agent agent){
        try {
            cancelService=new CancelService(agent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        cancelService.getCancelTicketInfo(SystemConfig.getLastOrderId());
        // Add initial rows or call a method to load data
        cancelService.getObserverList().forEach(ticket -> {
            addRow(gridView.getRowCount(), ticket.getTicketId(), ticket.getInStation(), ticket.getOutStation(), ticket.getTicketType(),
                    TimeUtil.epochMilliToFormattedSystemTime(String.valueOf(ticket.getIssueAt()),"dd-MM-yyyy HH:mm:ss"));
        });
        if(cancelService.getObserverList().isEmpty())
            clearButton.setDisable(true);
    }

    private void addRow(int rowIndex, String col1Text, String col2Text, String col3Text, String col4Text, String col5Text) {
        gridView.add(new Label(col1Text), 0, rowIndex);
        gridView.add(new Label(col2Text), 1, rowIndex);
        gridView.add(new Label(col3Text), 2, rowIndex);
        gridView.add(new Label(col4Text), 3, rowIndex);
        gridView.add(new Label(col5Text), 4, rowIndex);

        // Apply styles to the new row
        for (int col = 0; col < 5; col++) {
            ((Label) gridView.getChildren().get(gridView.getChildren().size() - 5 + col)).getStyleClass().add("text");
        }
    }

    public void onClearClick(MouseEvent mouseEvent) {

        System.out.println("Clearing the grid view");
        cancelService.markTicketCancelByOrderId(SystemConfig.getLastOrderId());
//        TODO: agent.getScuService().markLastOrderCancel();
        gridView.setVisible(false);

    }
}

