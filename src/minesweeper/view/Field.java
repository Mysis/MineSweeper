package minesweeper.view;

import javafx.beans.binding.IntegerBinding;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import minesweeper.model.FieldModel;
import minesweeper.model.GameConstants;

public class Field extends GridPane {
    
    FieldModel fieldModel;
    AppearanceValues appearanceValues;
    
    public Field(GameConstants gameConstants, AppearanceValues appearanceValues, IntegerBinding size) {
        this.appearanceValues = appearanceValues;
        //spacing between cells
        setHgap(1);
        setVgap(1);
        newGame(gameConstants, size);
    }
    
    public void newGame(GameConstants gameConstants, IntegerBinding size) {
        //create new model
        fieldModel = new FieldModel(gameConstants);
        
        //create new cells for new game
        for (int x = 0; x < fieldModel.cells().size(); x++) {
            for (int y = 0; y < fieldModel.cells().get(x).size(); y++) {
                Cell cell = new Cell(size, fieldModel, fieldModel.cells().get(x).get(y), appearanceValues);
                add(cell, x, y);
            }
        }
        
        for (Node node : getChildren()) {
            if (node instanceof Cell) {
                //add content if node is a cell
                ((Cell) node).addContent();
            }
        }
    }
    
    public FieldModel getModel() {
        return fieldModel;
    }
}