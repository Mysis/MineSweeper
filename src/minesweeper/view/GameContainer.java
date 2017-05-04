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
        paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(appearanceValues.gameBoundarySizeProperty().get()), appearanceValues.gameBoundarySizeProperty()));
    }
    
    public IntegerBinding calculateSize() {
        IntegerBinding calculateSize = new IntegerBinding() {
            {
                super.bind(prefWidthProperty(), prefHeightProperty(), appearanceValues.gameBoundarySizeProperty());
            }
            @Override
            protected int computeValue() {
                int width = (int) ((prefWidthProperty().get() - gameConstants.columns - 2 * appearanceValues.gameBoundarySizeProperty().get()) / gameConstants.columns) - 1;
                int height = (int) ((prefHeightProperty().get() - gameConstants.rows - 2 * appearanceValues.gameBoundarySizeProperty().get()) / gameConstants.rows) - 1;
                if (width > height) {
                    return height;
                } else {
                    return width;
                }
            }
        };
        return calculateSize;
    }
    
    public void newGame() {
        if (game != null) {
            getChildren().remove(game);
        }
        
        game = new Field(gameConstants, appearanceValues, calculateSize());
        
        getChildren().add(game);
        game.setAlignment(Pos.CENTER);
    }
    
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