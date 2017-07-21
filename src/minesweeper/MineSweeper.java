package minesweeper;

import java.io.Serializable;
import java.util.ListIterator;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import minesweeper.menus.AppearanceSettings;
import minesweeper.model.GameConstants;
import minesweeper.menus.GameMenus;
import minesweeper.menus.GameSettings;
import minesweeper.menus.HighScores;
import minesweeper.util.HighScoresSave;
import minesweeper.util.TimeConverter;
import minesweeper.view.AppearanceValues;
import minesweeper.view.GameContainer;
import minesweeper.view.StatusBar;

public class MineSweeper extends Application implements Serializable {

    GameMenus menu;
    GameSettings gameSettings;
    AppearanceSettings appearanceSettings;
    HighScores highScores;
    StatusBar statusBar;
    GameContainer gameContainer;
    VBox root;
    Scene mainScene;

    Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        //init game windows
        gameSettings = new GameSettings(primaryStage);
        appearanceSettings = new AppearanceSettings();
        highScores = new HighScores();

        //create game content
        root = new VBox();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        menu = new GameMenus();
        statusBar = new StatusBar(appearanceSettings.getConstants());
        gameContainer = new GameContainer(appearanceSettings.getConstants());
        root.getChildren().addAll(menu, statusBar, gameContainer);

        //init scene
        mainScene = new Scene(root);
        mainScene.fillProperty().bind(appearanceSettings.getConstants().backgroundColorProperty());

        //bind gameContainer width and height to scene width and height
        gameContainer.prefWidthProperty().bind(mainScene.widthProperty());
        DoubleBinding prefHeight = new DoubleBinding() {
            {
                super.bind(mainScene.heightProperty(), root.getChildren());
            }
            @Override
            protected double computeValue() {
                double value = mainScene.heightProperty().get();
                for (Node node : root.getChildren()) {
                    if (node instanceof Region) {
                        if (node != gameContainer) {
                            value -= ((Region) node).heightProperty().get();
                        }
                    }
                }
                return value;
            }
        };
        gameContainer.prefHeightProperty().bind(prefHeight);

        //set actions for menu options
        menu.newGameItem().setOnAction(e -> newGame());
        menu.optionsItem().setOnAction(e -> {
            gameSettings.showWindow();
            if (!gameContainer.getGameConstants().equals(gameSettings.getSettings())) {
                newGame(gameSettings.getSettings());
            }
        });
        menu.changeAppearanceItem().setOnAction(e -> appearanceSettings.showWindow());
        menu.highScoresItem().setOnAction(e -> highScores.showWindow());
        menu.exitItem().setOnAction(e -> primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));

        //create confirmation dialog when closing if a game is in progress
        primaryStage.setOnCloseRequest(event -> {
            if (!gameContainer.getGame().getModel().getGameOver() && !gameContainer.getGame().getModel().getFirstCell()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit Minesweeper");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to exit?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.CANCEL) {
                    event.consume();
                }
            }
            System.exit(0);
        });

        //show window
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        //create first game
        newGame(gameSettings.getSettings());
    }

    //start a new game using existing settings
    public void newGame() {
        //create new game
        gameContainer.newGame();
        statusBar.newGame(gameContainer.getGame().getModel());

        //add listener to call gameOver(...) when game is over
        gameContainer.getGame().getModel().gameOverProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal) {
                gameOver(gameContainer.getGame().getModel().getWin(), statusBar.getTimeMillis(), gameSettings.getType());
            }
        });
    }

    //create a new game using new settings
    public void newGame(GameConstants constants) {
        //create new game
        gameContainer.newGame(constants);
        statusBar.newGame(gameContainer.getGame().getModel());
        
        //add listener to call gameOver(...) when game is over
        gameContainer.getGame().getModel().gameOverProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal) {
                gameOver(gameContainer.getGame().getModel().getWin(), statusBar.getTimeMillis(), gameSettings.getType());
            }
        });

        //calculate new stage min width and height
        double[] gameSize = AppearanceValues.calculateFieldStartSize(constants, appearanceSettings.getConstants());
        double extraHeight = 0;
        for (Node node : root.getChildren()) {
            if (node instanceof Region) {
                if (node != gameContainer) {
                    extraHeight += ((Region) node).heightProperty().get();
                }
            }
        }
        //add window decorations to min width and height
        primaryStage.setMinWidth(gameSize[0] + mainScene.getWindow().getWidth() - mainScene.getWidth());
        primaryStage.setMinHeight(gameSize[1] + mainScene.getWindow().getHeight() - mainScene.getHeight() + extraHeight);
    }

    //called when the game ends
    public void gameOver(boolean won, Long timeMillis, GameSettings.Type gameMode) {
        //get current highscores
        HighScoresSave scores = highScores.getScores();
        
        //check if new score is added
        boolean newScore = false;
        if (won) {
            newScore = highScores.addScoreIfPossible(gameMode, timeMillis);
            highScores.save();
        }
        
        //create dialog for game over message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //create stringbuilder to create content of dialog
        StringBuilder content = new StringBuilder("Time: " + TimeConverter.convertTimeMillisToString(timeMillis));
        if (gameMode != GameSettings.Type.CUSTOM) {
            if (newScore) {
                content.append("\n\nYou made a new highscore!");
            }
            content.append("\n\nHigh Scores:");
            ListIterator<Long> iterator = scores.getScoresOfType(gameMode).listIterator();
            while (iterator.hasNext()) {
                content.append("\n").
                        append((iterator.nextIndex() + 1)).
                        append(") ").
                        append(TimeConverter.convertTimeMillisToString(iterator.next()));
            }
        }
        alert.setContentText(content.toString());
        //set remaining content of dialog based on win or lose
        if (won) {
            alert.setTitle("Game Won");
            alert.setHeaderText("You win!");
        } else {
            alert.setTitle("Game Lost");
            alert.setHeaderText("You lose.");
        }
        alert.show();
    }
}
