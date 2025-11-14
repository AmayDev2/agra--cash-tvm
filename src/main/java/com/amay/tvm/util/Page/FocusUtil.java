package com.amay.tvm.util.Page;

import com.amay.tom.model.siftdata.ShiftManagementDTO;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FocusUtil {
    public static void configureFocus(Button firstButton, Button lastButton) {
        lastButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                Platform.runLater(firstButton::requestFocus);
            }
        });
    }

    public static void configureFocus(TableView reportTable, Button lastButton){
        if(reportTable.getItems().isEmpty()) return;
        lastButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                Platform.runLater(()->reportTable.getSelectionModel().selectFirst());
            }
        });
    }

    public static void focusFirst(Button firstButton) {
        if (firstButton == null) return;

        Platform.runLater(firstButton::requestFocus);
    }

    private static boolean isDescendantOf(Node node, Parent container) {
        while (node != null) {
            if (node == container) return true;
            node = node.getParent();
        }
        return false;
    }
}
