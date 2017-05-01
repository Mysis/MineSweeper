package minesweeper;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Properties;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import minesweeper.menus.AppearanceSettings;
import minesweeper.menus.GameMenus;
import minesweeper.menus.GameSettings;
import minesweeper.model.GameConstants;
import minesweeper.view.AppearanceConstants;
import minesweeper.view.GameContainer;
import minesweeper.view.StatusBar;

public class MineSweeper extends Application {

    GameMenus menu;
    GameSettings gameSettings;
    AppearanceSettings appearanceSettings;
    StatusBar statusBar;
    GameContainer gameContainer;
    VBox root;
    Scene mainScene;

    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        gameSettings = new GameSettings(primaryStage);
        appearanceSettings = new AppearanceSettings();

        root = new VBox();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        menu = new GameMenus();
        gameContainer = new GameContainer(appearanceSettings.getConstants());
        statusBar = new StatusBar(appearanceSettings.getConstants());
        root.getChildren().addAll(menu, statusBar, gameContainer);

        mainScene = new Scene(root);
        mainScene.fillProperty().bind(appearanceSettings.getConstants().backgroundColorProperty());

        menu.newGameItem().setOnAction(e -> newGame());
        menu.settingsItem().setOnAction(e -> {
            gameSettings.showWindow();
            if (!gameContainer.getGameConstants().equals(gameSettings.getSettings())) {
                newGame(gameSettings.getSettings());
            }
        });
        menu.changeAppearanceItem().setOnAction(e -> {
            appearanceSettings.showWindow();
        });
        menu.exitItem().setOnAction(e -> primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));

        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit Minesweeper");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to exit?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.CANCEL) {
                event.consume();
            } else {
                System.exit(0);
            }
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
                gameOver(gameContainer.getGame().getModel().getWin(), statusBar.getTimeMillis());
            }
        });
    }

    public void newGame(GameConstants constants) {
        gameContainer.newGame(constants);
        statusBar.newGame(gameContainer.getGame().getModel());
        
        gameContainer.getGame().getModel().gameOverProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal) {
                gameOver(gameContainer.getGame().getModel().getWin(), statusBar.getTimeMillis());
            }
        });

        double[] size = AppearanceConstants.calculateFieldStartSize(constants, appearanceSettings.getConstants());
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
    
    public void gameOver(boolean won, long timeMillis) {
        boolean beatHighscore = false;
        boolean newScore = false;
        long previousTime = 0;
        Properties properties = new Properties();
        try {
            File save = new File("save.txt");
            try {
                FileInputStream input = new FileInputStream(save);
                properties.load(input);
            } catch (FileNotFoundException e) {
                save.createNewFile();
                for (GameSettings.Type type : GameSettings.Type.values()) {
                    if (type != GameSettings.Type.CUSTOM) {
                        properties.setProperty(type.toString().toLowerCase().concat("_time"), "");
                    }
                }
            }
            
            GameSettings.Type gameMode = gameSettings.getType();
            try {
                previousTime = Long.parseLong(properties.getProperty(gameMode.toString().toLowerCase().concat("_time")));
            } catch (NumberFormatException e) {
                newScore = true;
            }
            if (won) {
                if ((newScore || previousTime > timeMillis) && gameMode != GameSettings.Type.CUSTOM) {
                    if (!newScore) {
                        beatHighscore = true;
                    }
                    properties.setProperty(gameMode.toString().toLowerCase().concat("_time"), String.valueOf(timeMillis));
                } 
                FileOutputStream output = new FileOutputStream(save);
                properties.store(output, "Minesweeper save");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Unable to read/write to save file.");
            alert.showAndWait();
            alert = new Alert(Alert.AlertType.INFORMATION);
            if (won) {
                alert.setTitle("Game Won");
                alert.setHeaderText("You win!");
            } else {
                alert.setTitle("Game Lost");
                alert.setHeaderText("You lose.");
            }
            alert.setContentText("Time: " + StatusBar.convertTimeMillisToString(timeMillis));
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        StringBuilder content = new StringBuilder("Time: " + StatusBar.convertTimeMillisToString(timeMillis));
        if (!newScore) {
            if (won && beatHighscore) {
                content.append("\n\nYou beat your previous time of ").
                        append(StatusBar.convertTimeMillisToString(previousTime)).
                        append("!");
            } else {
                content.append("\n\nYour highscore for this difficulty is ").
                        append(StatusBar.convertTimeMillisToString(previousTime)).
                        append(".");
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

    public static void main(String[] args) {
        launch(args);
    }
}
