package minesweeper;

import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import minesweeper.view.GameContainer;

public class MineSweeper extends Application {

    @Override
    public void start(Stage primaryStage) {

        VBox root = new VBox();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        GameMenus menu = new GameMenus();
        GameContainer gameContainer = new GameContainer();
        root.getChildren().addAll(menu, gameContainer);
        
        Scene scene = new Scene(root, Constants.BACKGROUND_COLOR);
        
        menu.newGameItem().setOnAction(e -> gameContainer.game().newGame(Constants.ROWS, Constants.COLUMNS, Constants.MINES, gameContainer.calculateSize()));
        menu.exitItem().setOnAction(e -> primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        
        gameContainer.prefWidthProperty().bind(scene.widthProperty());
        gameContainer.prefHeightProperty().bind(scene.heightProperty().subtract(menu.heightProperty()));
        
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
        primaryStage.setScene(scene);
        primaryStage.show();
        
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        gameContainer.getChildren().remove(0);
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
