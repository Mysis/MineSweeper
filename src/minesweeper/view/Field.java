package minesweeper.view;

import javafx.beans.binding.IntegerBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import minesweeper.model.FieldModel;
import minesweeper.model.GameConstants;

public class Field extends GridPane {
    
    FieldModel fieldModel;
    AppearanceConstants appearanceConstants;
    
    public Field(GameConstants gameConstants, AppearanceConstants appearanceConstants, IntegerBinding size) {
        this.appearanceConstants = appearanceConstants;
        newGame(gameConstants, size);
    }
    
    public void newGame(GameConstants gameConstants, IntegerBinding size) {
        fieldModel = new FieldModel(gameConstants);
        
        for (int x = 0; x < fieldModel.cells().size(); x++) {
            for (int y = 0; y < fieldModel.cells().get(x).size(); y++) {
                Cell cell = new Cell(size, fieldModel, fieldModel.cells().get(x).get(y), appearanceConstants);
                setMargin(cell, new Insets(1));
                add(cell, x, y);
            }
        }
        
        fieldModel.firstCellProperty().addListener(o -> {
            if (!fieldModel.getFirstCell()) {
                for (Node node : getChildren()) {
                    if (node instanceof Cell) {
                        ((Cell) node).addContent();
                    }
                }
            }
        });
        
        fieldModel.gameOverProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal) {
                if (fieldModel.getWin()) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Game Won");
                    alert.setHeaderText(null);
                    alert.setContentText("You win!");
                    alert.show();
                } else {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Game Lost");
                    alert.setHeaderText(null);
                    alert.setContentText("You lose.");
                    alert.show();
                }
            }
        });
    }
    
    public FieldModel getModel() {
        return fieldModel;
    }
}