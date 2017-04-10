package minesweeper;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import minesweeper.view.GameContainer;

public class MineSweeper extends Application {

    @Override
    public void start(Stage primaryStage) {

        VBox root = new VBox();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        Menus menu = new Menus();
        GameContainer game = new GameContainer();
        root.getChildren().addAll(menu, game);
        
        Scene scene = new Scene(root, Constants.BACKGROUND_COLOR);
        
        game.prefWidthProperty().bind(scene.widthProperty());
        game.prefHeightProperty().bind(scene.heightProperty().subtract(menu.heightProperty()));
        
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        game.getChildren().remove(0);
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
