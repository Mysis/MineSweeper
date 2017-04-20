package minesweeper;

import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import minesweeper.menus.AppearanceSettings;
import minesweeper.menus.GameMenus;
import minesweeper.menus.GameSettings;
import minesweeper.model.GameConstants;
import minesweeper.view.AppearanceConstants;
import minesweeper.view.GameContainer;

public class MineSweeper extends Application {
    
    GameMenus menu;
    GameSettings gameSettings;
    AppearanceSettings appearanceSettings;
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
        gameContainer = new GameContainer(appearanceSettings.getSettings());
        root.getChildren().addAll(menu, gameContainer);
        
        mainScene = new Scene(root);
        mainScene.fillProperty().bind(appearanceSettings.getSettings().backgroundColorProperty());
        
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
            }
        });
        
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(mainScene);
        primaryStage.show();
        
        newGame(gameSettings.getSettings());
    }
    
    public void newGame() {
        gameContainer.newGame();
    }
    
    public void newGame(GameConstants constants) {
        primaryStage.setMinWidth(0);
        primaryStage.setMinHeight(0);
        gameContainer.newGame(constants);
        double[] size = AppearanceConstants.calculateFieldStartSize(constants, appearanceSettings.getSettings());
        gameContainer.prefWidthProperty().unbind();
        gameContainer.prefHeightProperty().unbind();
        gameContainer.setPrefSize(size[0], size[1]);
        primaryStage.sizeToScene();
        gameContainer.prefWidthProperty().bind(mainScene.widthProperty());
        gameContainer.prefHeightProperty().bind(mainScene.heightProperty().subtract(menu.heightProperty()));
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public void startTimer(ObservableValue... toPrint) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(5000), (e) -> {
            for (ObservableValue value : toPrint) {
                System.out.println(String.valueOf(value));
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
