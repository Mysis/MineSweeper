package minesweeper.view;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
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
    
    Rectangle shape; //background of cell
    Polygon flag;
    IntegerBinding size;
    AppearanceValues appearanceValues;

    public Cell(IntegerBinding size, FieldModel fieldModel, CellModel cellModel, AppearanceValues appearanceValues) {

        this.size = size;
        this.fieldModel = fieldModel;
        this.cellModel = cellModel;
        this.appearanceValues = appearanceValues;
        
        //update cell if a mine is relocated
        InvalidationListener relocateCellListener = (o -> {
            removeContent();
            addContent();
        });
        cellModel.surroundingProperty().addListener(relocateCellListener);
        cellModel.mineProperty().addListener(relocateCellListener);

        //init background
        shape = new Rectangle();
        shape.widthProperty().bind(size);
        shape.heightProperty().bind(size);
        shape.fillProperty().bind(Bindings.when(cellModel.stateProperty().isEqualTo(State.REVEALED)).then(appearanceValues.cellRevealedColorProperty()).otherwise(appearanceValues.cellColorProperty()));
        getChildren().add(shape);
        
        //init flag with proportions
        flag = new Polygon(
                0, 0, 
                0.5, 0.25, //<-- ie 0.25 point is 25% down the entire size
                0, 0.5);
        flag.fillProperty().bind(appearanceValues.flagColorProperty());
        setAlignment(flag, Pos.CENTER);
        getChildren().add(flag);
        
        //scale flag based on size binding
        flag.scaleXProperty().bind(size);
        flag.scaleYProperty().bind(size);
        flag.visibleProperty().bind(cellModel.stateProperty().isEqualTo(State.FLAGGED));
        
        //set mouse click events
        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                cellModel.reveal();
            } else if (e.getButton() == MouseButton.SECONDARY) {
                cellModel.toggleFlag();
            }
        });
    }
    
    //colors of clue numbers
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
                return Color.STEELBLUE;
            case 7:
                return Color.BLACK;
            case 8:
                return Color.GREY;
            default :
                throw new IllegalArgumentException("Valid surrounding numbers are 1 - 8.");
        }
    }
    
    //add content that can change to cell (ie if mine is relocated)
    public void addContent() {
        if (!cellModel.getMine()) {
            //fill with number if not mine
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
            
            //create X on flag if is incorrectly placed at the end of the game
            Line topLeftBottomRight = new Line(-0.25, -0.25, 0.25, 0.25);
            topLeftBottomRight.setStrokeWidth(2);
            Line topRightBottomLeft = new Line(0.25, -0.25, -0.25, 0.25);
            topRightBottomLeft.setStrokeWidth(2);
            
            //is incorrectly placed
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
            //scale with size
            topLeftBottomRight.strokeProperty().bind(appearanceValues.cellRevealedColorProperty());
            topRightBottomLeft.strokeProperty().bind(appearanceValues.cellRevealedColorProperty());
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
            //create ellipse for mine
            Ellipse mine = new Ellipse(.25, .25);
            mine.fillProperty().bind(appearanceValues.mineColorProperty());
            setAlignment(mine, Pos.CENTER);
            getChildren().add(mine);
            mine.scaleXProperty().bind(size);
            mine.scaleYProperty().bind(size);
            mine.visibleProperty().bind(cellModel.stateProperty().isEqualTo(State.REVEALED));
        }
    }
    
    //remove content that can change (ie if mine is relocated)
    public void removeContent() {
        List<Node> save = new ArrayList<>();
        save.add(shape);
        save.add(flag);
        getChildren().setAll(save);
    }
}
