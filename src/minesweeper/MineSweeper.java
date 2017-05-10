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
        
        gameSettings = new GameSettings(primaryStage);
        appearanceSettings = new AppearanceSettings();
        highScores = new HighScores();

        root = new VBox();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        menu = new GameMenus();
        gameContainer = new GameContainer(appearanceSettings.getConstants());
        statusBar = new StatusBar(appearanceSettings.getConstants());
        root.getChildren().addAll(menu, statusBar, gameContainer);

        mainScene = new Scene(root);
        mainScene.fillProperty().bind(appearanceSettings.getConstants().backgroundColorProperty());

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

        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        newGame(gameSettings.getSettings());
    }

    public void newGame() {
        gameContainer.newGame();
        statusBar.newGame(gameContainer.getGame().getModel());

        gameContainer.getGame().getModel().gameOverProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal) {
                gameOver(gameContainer.getGame().getModel().getWin(), statusBar.getTimeMillis(), gameSettings.getType());
            }
        });
    }

    public void newGame(GameConstants constants) {
        gameContainer.newGame(constants);
        statusBar.newGame(gameContainer.getGame().getModel());

        gameContainer.getGame().getModel().gameOverProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal) {
                gameOver(gameContainer.getGame().getModel().getWin(), statusBar.getTimeMillis(), gameSettings.getType());
            }
        });

        double[] size = AppearanceValues.calculateFieldStartSize(constants, appearanceSettings.getConstants());
        primaryStage.setMinWidth(0);
        primaryStage.setMinHeight(0);
        gameContainer.prefWidthProperty().unbind();
        gameContainer.prefHeightProperty().unbind();
        gameContainer.setPrefSize(size[0], size[1]);
        primaryStage.sizeToScene();

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

        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }

    public void gameOver(boolean won, Long timeMillis, GameSettings.Type gameMode) {
        HighScoresSave scores = highScores.getScores();
        boolean newScore = false;
        if (won) {
            newScore = highScores.addScoreIfPossible(gameMode, timeMillis);
            highScores.save();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        StringBuilder content = new StringBuilder("Time: " + StatusBar.convertTimeMillisToString(timeMillis));
        if (gameMode != GameSettings.Type.CUSTOM) {
            if (newScore) {
                content.append("\n\nYou made a new highscore!");
            }
            content.append("\n\nHigh Scores:");
            ListIterator<Long> iterator = scores.getScoresOfType(gameMode).listIterator();
            while (iterator.hasNext()) {
                content.append("\n").
                        append((iterator.nextIndex() + 1)).
                        append(": ").
                        append(StatusBar.convertTimeMillisToString(iterator.next()));
            }
        }
        alert.setContentText(content.toString());
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
