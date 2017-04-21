package minesweeper.view;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import minesweeper.model.CellModel;
import minesweeper.model.CellModel.State;
import minesweeper.model.FieldModel;

public class Cell extends StackPane {

    FieldModel fieldModel;
    CellModel cellModel;
    
    Rectangle shape;
    Polygon flag;
    IntegerBinding size;
    AppearanceConstants appearanceConstants;

    public Cell(IntegerBinding size, FieldModel fieldModel, CellModel cellModel, AppearanceConstants appearanceConstants) {

        this.size = size;
        this.fieldModel = fieldModel;
        this.cellModel = cellModel;
        this.appearanceConstants = appearanceConstants;

        shape = new Rectangle();
        shape.widthProperty().bind(size);
        shape.heightProperty().bind(size);
        shape.fillProperty().bind(Bindings.when(cellModel.stateProperty().isEqualTo(State.REVEALED)).then(appearanceConstants.cellRevealedColorProperty()).otherwise(appearanceConstants.cellColorProperty()));
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
        flag.visibleProperty().bind(cellModel.stateProperty().isEqualTo(State.FLAGGED));
        
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
                surroundingText.setTextFill(getSurroundingColor(cellModel.getSurrounding()));
                surroundingText.setFont(Font.font("Arial", FontWeight.BOLD, USE_PREF_SIZE));
                surroundingText.textProperty().set(String.valueOf(cellModel.getSurrounding()));
                surroundingText.styleProperty().bind(Bindings.concat("-fx-font-size: ", size.divide(2).asString(), ";"));
                surroundingText.visibleProperty().bind(cellModel.stateProperty().isEqualTo(State.REVEALED));
            }
            
            Line topLeftBottomRight = new Line(-0.25, -0.25, 0.25, 0.25);
            topLeftBottomRight.setStrokeWidth(2);
            //topLeftBottomRight.setStroke(null);
            Line topRightBottomLeft = new Line(0.25, -0.25, -0.25, 0.25);
            topRightBottomLeft.setStrokeWidth(2);
            //topRightBottomLeft.setStroke(null);
            
            BooleanBinding incorrectFlag = new BooleanBinding() {
                {
                    super.bind(cellModel.stateProperty(), fieldModel.gameOverProperty());
                } 
                @Override
                public boolean computeValue() {
                    if (cellModel.stateProperty().get() == State.FLAGGED && fieldModel.gameOverProperty().get()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            topLeftBottomRight.strokeProperty().bind(appearanceConstants.cellRevealedColorProperty());
            topRightBottomLeft.strokeProperty().bind(appearanceConstants.cellRevealedColorProperty());
            topLeftBottomRight.startXProperty().bind(size.multiply(-0.25));
            topLeftBottomRight.startYProperty().bind(size.multiply(-0.25));
            topLeftBottomRight.endXProperty().bind(size.multiply(0.25));
            topLeftBottomRight.endYProperty().bind(size.multiply(0.25));
            topRightBottomLeft.startXProperty().bind(size.multiply(0.25));
            topRightBottomLeft.startYProperty().bind(size.multiply(-0.25));
            topRightBottomLeft.endXProperty().bind(size.multiply(-0.25));
            topRightBottomLeft.endYProperty().bind(size.multiply(0.25));
            topLeftBottomRight.visibleProperty().bind(incorrectFlag);
            topRightBottomLeft.visibleProperty().bind(incorrectFlag);
            setAlignment(topLeftBottomRight, Pos.CENTER);
            setAlignment(topRightBottomLeft, Pos.CENTER);
            getChildren().add(topLeftBottomRight);
            getChildren().add(topRightBottomLeft);
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
    
    private Color getSurroundingColor(int surrounding) {
        switch (surrounding) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.RED;
            case 4:
                return Color.PURPLE;
            case 5:
                return Color.MAROON;
            case 6:
                return Color.TURQUOISE;
            case 7:
                return Color.BLACK;
            case 8:
                return Color.GREY;
            default :
                throw new IllegalArgumentException("Valid surrounding numbers are 1 - 8.");
        }
    }
}
