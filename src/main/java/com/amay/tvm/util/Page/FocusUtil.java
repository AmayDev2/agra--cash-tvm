package com.amay.tvm.util.Page;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FocusUtil {
    public static void configureFocus(Parent root, Button firstButton, Button lastButton){
        root.addEventFilter(KeyEvent.KEY_PRESSED,event -> {
            if(event.getCode()== KeyCode.TAB){
                Node focusOwner = root.getScene().getFocusOwner();
                if(!isDescendantOf(focusOwner,root)){
                    event.consume();
                    Platform.runLater(firstButton::requestFocus);
                }
            }
        });

        firstButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB && event.isShiftDown()) {
                event.consume();
                Platform.runLater(lastButton::requestFocus);
            }
        });
    }

    private static boolean isDescendantOf(Node node, Parent container) {
        while (node != null) {
            if (node == container) return true;
            node = node.getParent();
        }
        return false;
    }
}
