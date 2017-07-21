package minesweeper.menus;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.EnumMap;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import minesweeper.model.GameConstants;
import minesweeper.util.MineSweeperFiles;
import minesweeper.util.NumberFilter;

public class GameSettings implements Serializable {
    
    //save file
    transient File save = new File("settings.ser");
    
    Type currentType;
    transient ToggleGroup gameTypeGroup;
    transient RadioButton beginner;
    transient RadioButton intermediate;
    transient RadioButton expert;
    transient RadioButton custom;
    
    transient TextField rows;
    transient TextField columns;
    transient TextField mines;
    
    transient Stage stage;
    
    EnumMap<Type, GameConstants> typeValues;
    
    //game type
    public enum Type {
        BEGINNER,
        INTERMEDIATE,
        EXPERT,
        CUSTOM
    }
    
    public GameSettings(Stage ownerStage) {
        
        //set values for each game type, with custom being changeable
        typeValues = new EnumMap<>(Type.class);
        typeValues.put(Type.BEGINNER, new GameConstants(9, 9, 10));
        typeValues.put(Type.INTERMEDIATE, new GameConstants(16, 16, 40));
        typeValues.put(Type.EXPERT, new GameConstants(16, 30, 99));
        typeValues.put(Type.CUSTOM, null);
        
        //init layout
        VBox root = new VBox(25);
        HBox grids = new HBox(55);
        VBox defaultGrids = new VBox(10);
        GridPane customGrid = new GridPane();
        HBox buttons = new HBox(15);
        grids.getChildren().addAll(defaultGrids, customGrid);
        root.getChildren().addAll(grids, buttons);
        root.setPadding(new Insets(20));
        
        //create radio buttons on left
        gameTypeGroup = new ToggleGroup();
        beginner = new RadioButton("Beginner: 9 x 9 grid, 10 mines.");
        beginner.setToggleGroup(gameTypeGroup);
        intermediate = new RadioButton("Intermediate: 16 x 16 grid, 40 mines.");
        intermediate.setToggleGroup(gameTypeGroup);
        expert = new RadioButton("Expert: 16 x 30 grid, 99 mines.");
        expert.setToggleGroup(gameTypeGroup);
        custom = new RadioButton("Custom");
        custom.setToggleGroup(gameTypeGroup);
        defaultGrids.getChildren().addAll(beginner, intermediate, expert, custom);
        
        //create fields for custom settings on right
        customGrid.setHgap(10);
        customGrid.setVgap(5);
        customGrid.add(new Label("Rows: "), 0, 0);
        customGrid.add(new Label("Columns: "), 0, 1);
        customGrid.add(new Label("Mines: "), 0, 2);
        
        rows = new TextField();
        rows.setPrefColumnCount(3);
        customGrid.add(rows, 1, 0);
        columns = new TextField();
        columns.setPrefColumnCount(3);
        customGrid.add(columns, 1, 1);
        mines = new TextField();
        mines.setPrefColumnCount(3);
        customGrid.add(mines, 1, 2);

        BooleanBinding invalidSettings = new BooleanBinding() {
            {
                super.bind(rows.textProperty(), columns.textProperty(), mines.textProperty());
            }
            @Override
            protected boolean computeValue() {
                if (!rows.getText().equals("") && !columns.getText().equals("") && !mines.getText().equals("")) {
                    if (Integer.parseInt(rows.getText()) * Integer.parseInt(columns.getText()) < Integer.parseInt(mines.getText())) {
                        return true;
                    }
                }
                return false;
            }
        };
        StringBinding invalidTextFill = Bindings.when(invalidSettings).then("-fx-text-fill: red;").otherwise("");
        rows.styleProperty().bind(invalidTextFill);
        columns.styleProperty().bind(invalidTextFill);
        mines.styleProperty().bind(invalidTextFill);
        Label invalidSettingsWarning = new Label("This board isn't possible");
        invalidSettingsWarning.setTextFill(Color.RED);
        customGrid.add(invalidSettingsWarning, 0, 3);
        invalidSettings.addListener((event, oldVal, newVal) -> {
            if (!newVal) {
                invalidSettingsWarning.setVisible(false);
            }
        });
        invalidSettingsWarning.setVisible(false);
        
        //only allow numbers in text field, maximum of three digits
        rows.setTextFormatter(new TextFormatter<>(new NumberFilter(3)));
        columns.setTextFormatter(new TextFormatter<>(new NumberFilter(3)));
        mines.setTextFormatter(new TextFormatter<>(new NumberFilter(3)));
        
        //disable custom fields if custom radio button is not selected
        customGrid.disableProperty().bind(Bindings.when(gameTypeGroup.selectedToggleProperty().isEqualTo(custom)).then(false).otherwise(true));
        
        //init buttons
        Button ok = new Button("OK");
        ok.setPrefWidth(125);
        ok.setDefaultButton(true);
        Button cancel = new Button("Cancel");
        cancel.setPrefWidth(125);
        cancel.setCancelButton(true);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        buttons.getChildren().addAll(ok, cancel);
        
        //load save if exists
        if (save.exists()) {
            loadSettings(save);
        } else {
            currentType = Type.BEGINNER;
            gameTypeGroup.selectToggle(beginner);
        }
        
        //disable ok until all custom fields are filled, if custom is selected
        ok.disableProperty().bind(Bindings.when(
                Bindings.and(gameTypeGroup.selectedToggleProperty().isEqualTo(custom), 
                Bindings.or(Bindings.or(rows.textProperty().isEmpty(), columns.textProperty().isEmpty()), mines.textProperty().isEmpty())))
                .then(true).otherwise(false));
        
        //set actions for buttons
        ok.setOnAction(e -> {
            if (gameTypeGroup.getSelectedToggle() == beginner) {
                currentType = Type.BEGINNER;
            } else if (gameTypeGroup.getSelectedToggle() == intermediate) {
                currentType = Type.INTERMEDIATE;
            } else if (gameTypeGroup.getSelectedToggle() == expert) {
                currentType = Type.EXPERT;
            } else if (gameTypeGroup.getSelectedToggle() == custom) {
                if (!invalidSettings.get()) {
                    currentType = Type.CUSTOM;
                    typeValues.put(Type.CUSTOM, new GameConstants(Integer.parseInt(rows.getText()), Integer.parseInt(columns.getText()), Integer.parseInt(mines.getText())));
                } else {
                    invalidSettingsWarning.setVisible(true);
                    return;
                }
            }
            saveSettings(save);
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        });
        cancel.setOnAction(e -> {
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        });
        
        Scene scene = new Scene(root);
        stage = new Stage();

        stage.setOnCloseRequest(e -> invalidSettingsWarning.setVisible(false));
        
        //restrict main window if this window is open
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setResizable(false);
        stage.setTitle("Minesweeper Settings");
    }
    
    //convert button to enum
    private RadioButton typeToButton(Type type) {
        if (type == Type.BEGINNER) {
            return beginner;
        } else if (type == Type.INTERMEDIATE) {
            return intermediate;
        } else if (type == Type.EXPERT) {
            return expert;
        } else if (type == Type.CUSTOM) {
            return custom;
        } else {
            throw new IllegalArgumentException("Type has no associated button.");
        }
    }
    //convert enum to button
    private Type buttonToType(RadioButton button) {
        if (button == beginner) {
            return Type.BEGINNER;
        } else if (button == intermediate) {
            return Type.INTERMEDIATE;
        } else if (button == expert) {
            return Type.EXPERT;
        } else if (button == custom) {
            return Type.CUSTOM;
        } else {
            throw new IllegalArgumentException("Button not found");
        }
    }
    
    public void showWindow() {
        stage.showAndWait();
    }
    
    //save settings
    private void saveSettings(File file) {
        try {
            MineSweeperFiles.writeSerializedObject(this, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //load settings
    private void loadSettings(File file) {
        try {
            GameSettings oldSettings = (GameSettings) MineSweeperFiles.readSerializedFile(file);
            currentType = oldSettings.currentType;
            gameTypeGroup.selectToggle(typeToButton(currentType));
            GameConstants oldCustom = oldSettings.typeValues.get(Type.CUSTOM);
            typeValues.put(Type.CUSTOM, oldCustom);
            if (oldCustom != null) {
                rows.setText(String.valueOf(oldCustom.rows));
                columns.setText(String.valueOf(oldCustom.columns));
                mines.setText(String.valueOf(oldCustom.mines));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public Type getType() {
        return currentType;
    }
    public GameConstants getSettings() {
        return typeValues.get(currentType);
    }
}