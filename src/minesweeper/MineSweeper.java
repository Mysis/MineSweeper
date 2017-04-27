package minesweeper;

import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
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
            if (gameSettings.didSettingsChange()) {
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
            if (result.get() == ButtonType.CANCEL){
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
    }
    
    public void newGame(GameConstants constants) {
        gameContainer.newGame(constants);
        statusBar.newGame(gameContainer.getGame().getModel());
        
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

    public static void main(String[] args) {
        launch(args);
    }
}
