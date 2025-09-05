package com.amay.tom.utils;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.tinylog.Logger;

public class GridPaneCloner {

    public static GridPane deepCopy(GridPane original) {
        GridPane clone = new GridPane();

        // Copy column constraints
        for (ColumnConstraints col : original.getColumnConstraints()) {
            ColumnConstraints copy = new ColumnConstraints();
            copy.setMinWidth(col.getMinWidth());
            copy.setPrefWidth(col.getPrefWidth());
            copy.setMaxWidth(col.getMaxWidth());
            copy.setPercentWidth(col.getPercentWidth());
            clone.getColumnConstraints().add(copy);
        }

        // Copy row constraints
        for (RowConstraints row : original.getRowConstraints()) {
            RowConstraints copy = new RowConstraints();
            copy.setMinHeight(row.getMinHeight());
            copy.setPrefHeight(row.getPrefHeight());
            copy.setMaxHeight(row.getMaxHeight());
            copy.setValignment(row.getValignment());
            copy.setVgrow(row.getVgrow());
            clone.getRowConstraints().add(copy);
        }

        // Copy all child nodes
        for (Node node : original.getChildren()) {
            try {
                Node copied = copyNode(node);

                Integer col = GridPane.getColumnIndex(node);
                Integer row = GridPane.getRowIndex(node);
                Integer colSpan = GridPane.getColumnSpan(node);
                Integer rowSpan = GridPane.getRowSpan(node);

                if (col == null) col = 0;
                if (row == null) row = 0;

                if (copied != null) {
                    clone.add(copied, col, row,
                            colSpan != null ? colSpan : 1,
                            rowSpan != null ? rowSpan : 1);
                }

            } catch (Exception e) {
                Logger.error("Error copying node at col/row: {}", e.getMessage());
            }
        }

        return clone;
    }

    private static Node copyNode(Node node) {
        if (node instanceof Text) {
            Text original = (Text) node;
            Text copy = new Text(original.getText());
            copy.setFont(original.getFont());
            copy.getStyleClass().addAll(original.getStyleClass());

            StackPane wrapper = new StackPane(copy);
            wrapper.setBorder(new Border(new BorderStroke(
                    Color.BLACK,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths.DEFAULT
            )));
            wrapper.setPrefHeight(30);
            GridPane.setHalignment(wrapper, HPos.CENTER);
            return wrapper;
        }

        if (node instanceof BorderPane) {
            BorderPane original = (BorderPane) node;
            BorderPane copy = new BorderPane();
            copy.setPrefHeight(original.getPrefHeight());
            copy.setBorder(original.getBorder());
            return copy;
        }

        Logger.warn("Unsupported node type: " + node.getClass().getSimpleName());
        return null;
    }
}
