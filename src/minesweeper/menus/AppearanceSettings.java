package minesweeper.menus;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import minesweeper.util.MineSweeperFiles;
import minesweeper.view.AppearanceValues;

public class AppearanceSettings implements Serializable {
    
    File save = new File("appearance.ser");
    
    ColorPicker cell;
    ColorPicker cellRevealed;
    ColorPicker mine;
    ColorPicker flag;
    ColorPicker background;
    ColorPicker statusBar;
    
    AppearanceValues appearanceValues;
    AppearanceValues previous;
    Stage stage;
    
    public AppearanceSettings() {
        
        VBox root = new VBox(15);
        GridPane colors = new GridPane();
        HBox buttons = new HBox(15);
        
        root.getChildren().addAll(colors, buttons);
        root.setPadding(new Insets(20));

        colors.setHgap(20);
        colors.setVgap(10);

        colors.add(new Label("Cell color:"), 0, 0);
        colors.add(new Label("Cell revealed color:"), 0, 1);
        colors.add(new Label("Mine color:"), 0, 2);
        colors.add(new Label("Flag color:"), 2, 0);
        colors.add(new Label("Background color:"), 2, 1);
        colors.add(new Label("Status bar color:"), 2, 2);
        
        if (save.exists()) {
            loadValues(save);
        } else {
            appearanceValues = AppearanceValues.defaultValues();
        }
        cell = new ColorPicker(appearanceValues.getCellColor());
        appearanceValues.cellColorProperty().bindBidirectional(cell.valueProperty());
        colors.add(cell, 1, 0);
        cellRevealed = new ColorPicker(appearanceValues.getCellRevealedColor());
        appearanceValues.cellRevealedColorProperty().bindBidirectional(cellRevealed.valueProperty());
        colors.add(cellRevealed, 1, 1);
        mine = new ColorPicker(appearanceValues.getMineColor());
        appearanceValues.mineColorProperty().bindBidirectional(mine.valueProperty());
        colors.add(mine, 1, 2);
        flag = new ColorPicker(appearanceValues.getFlagColor());
        appearanceValues.flagColorProperty().bindBidirectional(flag.valueProperty());
        colors.add(flag, 3, 0);
        background = new ColorPicker(appearanceValues.getBackgroundColor());
        appearanceValues.backgroundColorProperty().bindBidirectional(background.valueProperty());
        colors.add(background, 3, 1);
        statusBar = new ColorPicker(appearanceValues.getStatusBarColor());
        appearanceValues.statusBarColorProperty().bindBidirectional(statusBar.valueProperty());
        colors.add(statusBar, 3, 2);
        
        if (save.exists()) {
            loadCustoms(save);
        }
        
        previous = new AppearanceValues(appearanceValues);
        
        Button ok = new Button("OK");
        ok.setPrefWidth(125);
        ok.setDefaultButton(true);
        Button defaults = new Button("Restore Defaults");
        defaults.setPrefWidth(125);
        Button cancel = new Button("Cancel");
        cancel.setPrefWidth(125);
        cancel.setCancelButton(true);
        buttons.getChildren().addAll(ok, defaults, cancel);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        
        ok.setOnAction(e -> {
            saveValues();
            stage.close();
        });
        defaults.setOnAction(e -> appearanceValues.setValues(AppearanceValues.defaultValues()));
        cancel.setOnAction(e -> {
            appearanceValues.setValues(previous);
            stage.close();
        });

        Scene scene = new Scene(root);

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setTitle("Appearance Settings");
    }
    
    private void saveValues() {
        List<ObservableList<Color>> colors = new ArrayList<>();
        Collections.addAll(colors, cell.getCustomColors(), cellRevealed.getCustomColors(), mine.getCustomColors(), flag.getCustomColors(), background.getCustomColors(), statusBar.getCustomColors());
        AppearanceSettingsSave appearanceSettingsSave = new AppearanceSettingsSave(appearanceValues, colors);
        try {
            MineSweeperFiles.writeSerializedObject(appearanceSettingsSave, save);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadValues(File file) {
        try {
            AppearanceSettingsSave loadSave = (AppearanceSettingsSave) MineSweeperFiles.readSerializedFile(file);
            AppearanceValues oldValues = loadSave.createAppearanceValuesFromSave();
            appearanceValues = new AppearanceValues(oldValues);
        } catch (IOException | ClassNotFoundException e) {
            appearanceValues = AppearanceValues.defaultValues();
            e.printStackTrace();
        }
    }
    
    private void loadCustoms(File file) {
        try {
            AppearanceSettingsSave loadSave = (AppearanceSettingsSave) MineSweeperFiles.readSerializedFile(file);
            List<List<Color>> oldCustomColors = new ArrayList<>();
            for (int i = 0; i < loadSave.customColors.size(); i++) {
                oldCustomColors.add(new ArrayList<>());
                for (String colorString : loadSave.customColors.get(i)) {
                    oldCustomColors.get(i).add(Color.valueOf(colorString));
                }
            }
            cell.getCustomColors().addAll(oldCustomColors.get(0));
            cellRevealed.getCustomColors().addAll(oldCustomColors.get(1));
            mine.getCustomColors().addAll(oldCustomColors.get(2));
            flag.getCustomColors().addAll(oldCustomColors.get(3));
            background.getCustomColors().addAll(oldCustomColors.get(4));
            statusBar.getCustomColors().addAll(oldCustomColors.get(5));
        } catch (IOException | ClassNotFoundException e) {
            appearanceValues = AppearanceValues.defaultValues();
            e.printStackTrace();
        }
    }
    
    public void showWindow() {
        previous.setValues(appearanceValues);
        stage.showAndWait();
    }
    
    public AppearanceValues getConstants() {
        return appearanceValues;
    }
}