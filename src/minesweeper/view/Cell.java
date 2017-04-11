package minesweeper.view;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import minesweeper.model.CellModel;
import minesweeper.model.CellModel.State;
import minesweeper.Constants;

public class Cell extends StackPane {

    CellModel cellModel;

    public Cell(IntegerBinding size, CellModel model) {

        cellModel = model;

        Rectangle shape = new Rectangle(size.get(), size.get());
        shape.widthProperty().bind(size);
        shape.heightProperty().bind(size);
        shape.fillProperty().bind(Bindings.when(Bindings.createBooleanBinding(() -> model.stateProperty().get() == State.REVEALED, model.stateProperty())).then(Constants.CELL_REVEALED_COLOR).otherwise(Constants.CELL_COLOR));
        getChildren().add(shape);
        
        Polygon flag = new Polygon(
                0, 0, 
                0.5, 0.25, 
                0, 0.5);
        flag.setFill(Constants.FLAG_COLOR);
        setAlignment(flag, Pos.CENTER);
        getChildren().add(flag);
        
        flag.scaleXProperty().bind(size);
        flag.scaleYProperty().bind(size);
        flag.visibleProperty().bind(Bindings.createBooleanBinding(() -> model.stateProperty().get() == State.FLAGGED, model.stateProperty()));
        
        if (!model.getMine()) {
            if (model.getSurrounding() > 0) {
                Label surroundingText = new Label();
                setAlignment(surroundingText, Pos.CENTER);
                getChildren().add(surroundingText);
                surroundingText.textProperty().set(String.valueOf(model.getSurrounding()));
                //surroundingText.textProperty().bind(Bindings.when(Bindings.createBooleanBinding(() -> model.surroundingProperty().get() != 0, model.surroundingProperty())).then(model.surroundingProperty().asString()).otherwise(""));
                surroundingText.styleProperty().bind(Bindings.concat("-fx-font-size: ", size.divide(2).asString(), ";"));
                surroundingText.visibleProperty().bind(Bindings.createBooleanBinding(() -> model.stateProperty().get() == State.REVEALED, model.stateProperty()));
            }
        } else {
            Ellipse mine = new Ellipse(.25, .25);
            mine.setFill(Constants.MINE_COLOR);
            setAlignment(mine, Pos.CENTER);
            getChildren().add(mine);
            mine.scaleXProperty().bind(size);
            mine.scaleYProperty().bind(size);
            mine.visibleProperty().bind(Bindings.createBooleanBinding(() -> model.stateProperty().get() == State.REVEALED, model.stateProperty()));
        }
        
        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                cellModel.reveal();
            } else if (e.getButton() == MouseButton.SECONDARY) {
                cellModel.toggleFlag();
            }
        });
    }
}
