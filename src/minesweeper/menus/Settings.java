package minesweeper.menus;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import minesweeper.model.GameConstants;

public class Settings {
    
    boolean changed = false;
    GameConstants gameConstants;
    Stage stage;
    
    public Settings(Stage ownerStage) {
        
        VBox root = new VBox(25);
        HBox grids = new HBox(55);
        VBox defaultGrids = new VBox(10);
        GridPane customGrid = new GridPane();
        HBox buttons = new HBox(15);
        grids.getChildren().addAll(defaultGrids, customGrid);
        root.getChildren().addAll(grids, buttons);
        
        root.setPadding(new Insets(20));
        
        ToggleGroup defaultsGroup = new ToggleGroup();
        RadioButton beginner = new RadioButton("Beginner: 9 x 9 grid, 10 mines.");
        beginner.setToggleGroup(defaultsGroup);
        RadioButton intermediate = new RadioButton("Intermediate: 16 x 16 grid, 40 mines.");
        intermediate.setToggleGroup(defaultsGroup);
        RadioButton expert = new RadioButton("Expert: 16 x 30 grid, 99 mines.");
        expert.setToggleGroup(defaultsGroup);
        RadioButton custom = new RadioButton("Custom");
        custom.setToggleGroup(defaultsGroup);
        defaultsGroup.selectToggle(beginner);
        gameConstants = new GameConstants(9, 9, 10);
        defaultGrids.getChildren().addAll(beginner, intermediate, expert, custom);
        
        customGrid.setHgap(10);
        customGrid.setVgap(5);
        customGrid.add(new Label("Rows: "), 0, 0);
        customGrid.add(new Label("Columns: "), 0, 1);
        customGrid.add(new Label("Mines: "), 0, 2);
        
        TextField rows = new TextField();
        rows.setPrefColumnCount(3);
        rows.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        customGrid.add(rows, 1, 0);
        TextField columns = new TextField();
        columns.setPrefColumnCount(3);
        columns.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        customGrid.add(columns, 1, 1);
        TextField mines = new TextField();
        mines.setPrefColumnCount(3);
        mines.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter()));
        customGrid.add(mines, 1, 2);
        
        customGrid.disableProperty().bind(Bindings.when(defaultsGroup.selectedToggleProperty().isEqualTo(custom)).then(false).otherwise(true));
        
        Button ok = new Button("OK");
        ok.setPrefWidth(125);
        ok.setDefaultButton(true);
        Button cancel = new Button("Cancel");
        cancel.setPrefWidth(125);
        cancel.setCancelButton(true);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        buttons.getChildren().addAll(ok, cancel);
        
        ok.disableProperty().bind(Bindings.when(
                Bindings.and(defaultsGroup.selectedToggleProperty().isEqualTo(custom), 
                Bindings.or(Bindings.or(rows.textProperty().isEmpty(), columns.textProperty().isEmpty()), mines.textProperty().isEmpty())))
                .then(true).otherwise(false));
        
        ok.setOnAction(e -> {
            if (defaultsGroup.getSelectedToggle() == beginner) {
                gameConstants = new GameConstants(9, 9, 10);
            } else if (defaultsGroup.getSelectedToggle() == intermediate) {
                gameConstants = new GameConstants(16, 16, 40);
            } else if (defaultsGroup.getSelectedToggle() == expert) {
                gameConstants = new GameConstants(16, 30, 99);
            } else if (defaultsGroup.getSelectedToggle() == custom) {
                gameConstants = new GameConstants(Integer.parseInt(rows.getText()), Integer.parseInt(columns.getText()), Integer.parseInt(mines.getText()));
            }
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
            changed = true;
        });
        cancel.setOnAction(e -> {
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        });
        
        Scene scene = new Scene(root);
        stage = new Stage();
        
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setResizable(false);
        stage.setTitle("Minesweeper Settings");
    }
    
    public void showWindow() {
        stage.showAndWait();
    }
    
    public boolean didSettingsChange() {
        return changed;
    }
    
    public GameConstants getSettings() {
        changed = false;
        return gameConstants;
    }
}