package minesweeper.menus;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import minesweeper.model.GameConstants;

public class GameSettings {
    
    boolean changed = false;
    GameConstants gameConstants;
    Stage stage;
    
    public GameSettings(Stage ownerStage) {
        
        gameConstants = new GameConstants(9, 9, 10);
        
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
        defaultGrids.getChildren().addAll(beginner, intermediate, expert, custom);
        
        customGrid.setHgap(10);
        customGrid.setVgap(5);
        customGrid.add(new Label("Rows: "), 0, 0);
        customGrid.add(new Label("Columns: "), 0, 1);
        customGrid.add(new Label("Mines: "), 0, 2);
        
        TextField rows = new TextField();
        rows.setPrefColumnCount(3);
        customGrid.add(rows, 1, 0);
        TextField columns = new TextField();
        columns.setPrefColumnCount(3);
        customGrid.add(columns, 1, 1);
        TextField mines = new TextField();
        mines.setPrefColumnCount(3);
        
        rows.addEventFilter(KeyEvent.KEY_TYPED, letterFilter(3));
        columns.addEventFilter(KeyEvent.KEY_TYPED, letterFilter(3));
        mines.addEventFilter(KeyEvent.KEY_TYPED, letterFilter(3));
        
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
    
    private EventHandler<KeyEvent> letterFilter(final Integer maxLength) {
        return e -> {
            TextField txt_TextField = (TextField) e.getSource();                
            if (txt_TextField.getText().length() >= maxLength) {                    
                e.consume();
            }
            if (e.getCharacter().matches("[0-9.]")){ 
                if (txt_TextField.getText().contains(".") && e.getCharacter().matches("[.]")){
                    e.consume();
                }else if (txt_TextField.getText().length() == 0 && e.getCharacter().matches("[.]")){
                    e.consume(); 
                }
            } else {
                e.consume();
            }
        };
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