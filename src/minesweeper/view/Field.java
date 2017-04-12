package minesweeper.view;

import minesweeper.model.FieldModel;
import javafx.beans.binding.IntegerBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

public class Field extends GridPane {
    
    FieldModel fieldModel;
    
    public Field(int rows, int columns, int mines, IntegerBinding size) throws ArithmeticException {
        
        if (mines > rows * columns) {
            throw new ArithmeticException("The board isn't big enough for all the mines");
        }
        
        newGame(rows, columns, mines, size);
    }
    
    public void newGame(int rows, int columns, int mines, IntegerBinding size) {
        fieldModel = new FieldModel(rows, columns, mines);
        
        for (int x = 0; x < fieldModel.cells().size(); x++) {
            for (int y = 0; y < fieldModel.cells().get(x).size(); y++) {
                Cell cell = new Cell(size, fieldModel.cells().get(x).get(y));
                setMargin(cell, new Insets(1));
                add(cell, x, y);
            }
        }
        
        fieldModel.gameOverProperty().addListener(o -> {
            if (fieldModel.getWin()) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Game Won");
                alert.setHeaderText(null);
                alert.setContentText("You win!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Game Lost");
                alert.setHeaderText(null);
                alert.setContentText("You lose.");
                alert.showAndWait();
            }
        });
    }
    
    public FieldModel model() {
        return fieldModel;
    }
}