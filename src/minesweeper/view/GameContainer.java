package minesweeper.view;

import javafx.beans.binding.IntegerBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import minesweeper.Constants;
import minesweeper.model.GameConstants;

public class GameContainer extends StackPane {
    
    Field game;
    Rectangle background;
    GameConstants gameConstants;

    public GameContainer(GameConstants constants) {
        double[] gameDimensions = Constants.calculateFieldStartSize(constants);
        background = new Rectangle(gameDimensions[0], gameDimensions[1]);
        //background.setFill(Color.TRANSPARENT);
        getChildren().add(background);
        newGame(constants);
    }
    
    public IntegerBinding calculateSize() {
        IntegerBinding calculateSize = new IntegerBinding() {
            {
                super.bind(prefWidthProperty(), prefHeightProperty());
            }
            @Override
            protected int computeValue() {
                int width = (int) ((prefWidthProperty().get() - gameConstants.columns - 2 * Constants.GAME_BOUNDARY_SIZE) / gameConstants.columns) - 1;
                int height = (int) ((prefHeightProperty().get() - gameConstants.rows - 2 * Constants.GAME_BOUNDARY_SIZE) / gameConstants.rows) - 1;
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
        
        game = new Field(gameConstants, calculateSize());
        
        getChildren().add(game);
        game.setAlignment(Pos.CENTER);
        setMargin(game, new Insets(Constants.GAME_BOUNDARY_SIZE));
    }
    
    public void newGame(GameConstants constants) {
        if (game != null) {
            getChildren().remove(game);
        }
        
        gameConstants = constants;
        game = new Field(gameConstants, calculateSize());
        
        getChildren().add(game);
        game.setAlignment(Pos.CENTER);
        setMargin(game, new Insets(Constants.GAME_BOUNDARY_SIZE));
    }
    
    public void removeBackground() {
        getChildren().remove(background);
        background = null;
    }
    
    public Field game() {
        return game;
    }
}