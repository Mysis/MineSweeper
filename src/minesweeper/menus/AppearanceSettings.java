package minesweeper.menus;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import minesweeper.view.AppearanceConstants;

public class AppearanceSettings {
    
    AppearanceConstants appearanceConstants;
    Stage stage;
    
    public AppearanceSettings() {
        
        appearanceConstants = AppearanceConstants.defaultConstants();
        
        GridPane colors = new GridPane();
        colors.setPadding(new Insets(15));
        colors.setHgap(20);
        colors.setVgap(10);
        
        colors.add(new Label("Cell color:"), 0, 0);
        colors.add(new Label("Cell revealed color:"), 0, 1);
        colors.add(new Label("Mine color:"), 0, 2);
        colors.add(new Label("Flag color:"), 0, 3);
        colors.add(new Label("Background color:"), 0, 4);
        
        ColorPicker cell = new ColorPicker(appearanceConstants.getCellColor());
        appearanceConstants.cellColorProperty().bind(cell.valueProperty());
        colors.add(cell, 1, 0);
        ColorPicker cellRevealed = new ColorPicker(appearanceConstants.getCellRevealedColor());
        appearanceConstants.cellRevealedColorProperty().bind(cellRevealed.valueProperty());
        colors.add(cellRevealed, 1, 1);
        ColorPicker mine = new ColorPicker(appearanceConstants.getMineColor());
        appearanceConstants.mineColorProperty().bind(mine.valueProperty());
        colors.add(mine, 1, 2);
        ColorPicker flag = new ColorPicker(appearanceConstants.getFlagColor());
        appearanceConstants.flagColorProperty().bind(flag.valueProperty());
        colors.add(flag, 1, 3);
        ColorPicker background = new ColorPicker(appearanceConstants.getBackgroundColor());
        appearanceConstants.backgroundColorProperty().bind(background.valueProperty());
        colors.add(background, 1, 4);
        
        Scene scene = new Scene(colors);
        stage = new Stage();
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Appearance Settings");
    }
    
    public void showWindow() {
        stage.showAndWait();
    }
    
    public AppearanceConstants getSettings() {
        return appearanceConstants;
    }
}