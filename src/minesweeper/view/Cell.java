package minesweeper.view;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import minesweeper.model.CellModel;
import minesweeper.model.CellModel.State;

public class Cell extends StackPane {

    CellModel cellModel;
    
    Rectangle shape;
    Polygon flag;
    IntegerBinding size;
    AppearanceConstants appearanceConstants;

    public Cell(IntegerBinding size, CellModel model, AppearanceConstants appearanceConstants) {

        this.size = size;
        cellModel = model;
        this.appearanceConstants = appearanceConstants;

        shape = new Rectangle();
        shape.widthProperty().bind(size);
        shape.heightProperty().bind(size);
        shape.fillProperty().bind(Bindings.when(model.stateProperty().isEqualTo(State.REVEALED)).then(appearanceConstants.cellRevealedColorProperty()).otherwise(appearanceConstants.cellColorProperty()));
        getChildren().add(shape);
        
        flag = new Polygon(
                0, 0, 
                0.5, 0.25, 
                0, 0.5);
        flag.fillProperty().bind(appearanceConstants.flagColorProperty());
        setAlignment(flag, Pos.CENTER);
        getChildren().add(flag);
        
        flag.scaleXProperty().bind(size);
        flag.scaleYProperty().bind(size);
        flag.visibleProperty().bind(model.stateProperty().isEqualTo(State.FLAGGED));
        
        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                cellModel.reveal();
            } else if (e.getButton() == MouseButton.SECONDARY) {
                cellModel.toggleFlag();
            }
        });
    }
    
    public void addContent() {
        if (!cellModel.getMine()) {
            if (cellModel.getSurrounding() > 0) {
                Label surroundingText = new Label();
                setAlignment(surroundingText, Pos.CENTER);
                getChildren().add(surroundingText);
                surroundingText.textProperty().set(String.valueOf(cellModel.getSurrounding()));
                surroundingText.styleProperty().bind(Bindings.concat("-fx-font-size: ", size.divide(2).asString(), ";"));
                surroundingText.visibleProperty().bind(cellModel.stateProperty().isEqualTo(State.REVEALED));
            }
        } else {
            Ellipse mine = new Ellipse(.25, .25);
            mine.fillProperty().bind(appearanceConstants.mineColorProperty());
            setAlignment(mine, Pos.CENTER);
            getChildren().add(mine);
            mine.scaleXProperty().bind(size);
            mine.scaleYProperty().bind(size);
            mine.visibleProperty().bind(cellModel.stateProperty().isEqualTo(State.REVEALED));
        }
    }
    
    public void removeContent() {
        List<Node> save = new ArrayList<>();
        save.add(shape);
        save.add(flag);
        getChildren().setAll(save);
    }
}
