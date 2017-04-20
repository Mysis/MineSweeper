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
    AppearanceConstants appearanceConstants;

    public GameContainer(AppearanceConstants appearanceConstants) {
        this.appearanceConstants = appearanceConstants;
        paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(appearanceConstants.gameBoundarySizeProperty().get()), appearanceConstants.gameBoundarySizeProperty()));
    }
    
    public IntegerBinding calculateSize() {
        IntegerBinding calculateSize = new IntegerBinding() {
            {
                super.bind(prefWidthProperty(), prefHeightProperty(), appearanceConstants.gameBoundarySizeProperty());
            }
            @Override
            protected int computeValue() {
                int width = (int) ((prefWidthProperty().get() - gameConstants.columns - 2 * appearanceConstants.gameBoundarySizeProperty().get()) / gameConstants.columns) - 1;
                int height = (int) ((prefHeightProperty().get() - gameConstants.rows - 2 * appearanceConstants.gameBoundarySizeProperty().get()) / gameConstants.rows) - 1;
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
        
        game = new Field(gameConstants, appearanceConstants, calculateSize());
        
        getChildren().add(game);
        game.setAlignment(Pos.CENTER);
    }
    
    public void newGame(GameConstants constants) {
        if (game != null) {
            getChildren().remove(game);
        }
        
        gameConstants = constants;
        game = new Field(gameConstants, appearanceConstants, calculateSize());
        
        getChildren().add(game);
        game.setAlignment(Pos.CENTER);
    }
    
    public Field game() {
        return game;
    }
}