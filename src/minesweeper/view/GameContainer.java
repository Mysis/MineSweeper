package minesweeper.view;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import minesweeper.model.GameConstants;

public class GameContainer extends StackPane {
    
    Field game;
    GameConstants gameConstants;
    AppearanceValues appearanceValues;

    public GameContainer(AppearanceValues appearanceValues) {
        this.appearanceValues = appearanceValues;
        //bind padding
        paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(appearanceValues.gameBoundarySizeProperty().get()), appearanceValues.gameBoundarySizeProperty()));
    }
    
    //binding that calculates size index for all cells
    public IntegerBinding calculateSize() {
        IntegerBinding calculateSize = new IntegerBinding() {
            {
                super.bind(prefWidthProperty(), prefHeightProperty(), appearanceValues.gameBoundarySizeProperty());
            }
            @Override
            protected int computeValue() {
                int width = (int) ((prefWidthProperty().get() - gameConstants.columns - 2 * appearanceValues.gameBoundarySizeProperty().get()) / gameConstants.columns) - 1;
                int height = (int) ((prefHeightProperty().get() - gameConstants.rows - 2 * appearanceValues.gameBoundarySizeProperty().get()) / gameConstants.rows) - 1;
                //return smaller value, so the grid is always visible (ie if there is room to expand the width, but if doing so would make the grid exceed the height, grid does not grow)
                if (width > height) {
                    return height;
                } else {
                    return width;
                }
            }
        };
        return calculateSize;
    }
    
    //start new game with old constants
    public void newGame() {
        //remove old game
        if (game != null) {
            getChildren().remove(game);
        }
        
        game = new Field(gameConstants, appearanceValues, calculateSize());
        
        getChildren().add(game);
        game.setAlignment(Pos.CENTER);
    }
    
    //new game with new constants
    public void newGame(GameConstants constants) {
        gameConstants = constants;
        newGame();
    }
    
    public Field getGame() {
        return game;
    }
    public GameConstants getGameConstants() {
        return gameConstants;
    }
}