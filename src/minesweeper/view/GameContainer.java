package minesweeper.view;

import javafx.beans.binding.IntegerBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import minesweeper.Constants;

public class GameContainer extends StackPane {
    
    Field game;

    public GameContainer() {
        
        Rectangle background = new Rectangle(Constants.FIELD_START_WIDTH, Constants.FIELD_START_HEIGHT);
        getChildren().add(background);
        
        game = new Field(Constants.ROWS, Constants.COLUMNS, Constants.MINES, calculateSize());
        
        getChildren().add(game);
        game.setAlignment(Pos.CENTER);
        setMargin(game, new Insets(Constants.GAME_BOUNDARY_SIZE));
    }
    
    public IntegerBinding calculateSize() {
        IntegerBinding calculateSize = new IntegerBinding() {
            {
                super.bind(prefWidthProperty(), prefHeightProperty());
            }
            @Override
            protected int computeValue() {
                if (prefHeightProperty().get() > prefWidthProperty().get()) {
                    return (int) ((prefWidthProperty().get() - (Constants.COLUMNS - 1) - 2 * Constants.GAME_BOUNDARY_SIZE) / Constants.COLUMNS) - 1;
                } else {
                    return (int) ((prefHeightProperty().get() - (Constants.ROWS - 1) - 2 * Constants.GAME_BOUNDARY_SIZE) / Constants.ROWS) - 1;
                }
            }
        };
        return calculateSize;
    }
    public Field game() {
        return game;
    }
}