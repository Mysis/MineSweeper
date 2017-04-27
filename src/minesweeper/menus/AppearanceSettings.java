package minesweeper.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import minesweeper.view.AppearanceConstants;

public class AppearanceSettings {
    
    AppearanceConstants appearanceConstants;
    AppearanceConstants previous;
    Stage stage;
    
    public AppearanceSettings() {
        
        appearanceConstants = AppearanceConstants.defaultConstants();
        previous = new AppearanceConstants(appearanceConstants);
        
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

        ColorPicker cell = new ColorPicker(appearanceConstants.getCellColor());
        appearanceConstants.cellColorProperty().bindBidirectional(cell.valueProperty());
        colors.add(cell, 1, 0);
        ColorPicker cellRevealed = new ColorPicker(appearanceConstants.getCellRevealedColor());
        appearanceConstants.cellRevealedColorProperty().bindBidirectional(cellRevealed.valueProperty());
        colors.add(cellRevealed, 1, 1);
        ColorPicker mine = new ColorPicker(appearanceConstants.getMineColor());
        appearanceConstants.mineColorProperty().bindBidirectional(mine.valueProperty());
        colors.add(mine, 1, 2);
        ColorPicker flag = new ColorPicker(appearanceConstants.getFlagColor());
        appearanceConstants.flagColorProperty().bindBidirectional(flag.valueProperty());
        colors.add(flag, 3, 0);
        ColorPicker background = new ColorPicker(appearanceConstants.getBackgroundColor());
        appearanceConstants.backgroundColorProperty().bindBidirectional(background.valueProperty());
        colors.add(background, 3, 1);
        ColorPicker statusBar = new ColorPicker(appearanceConstants.getStatusBarColor());
        appearanceConstants.statusBarColorProperty().bindBidirectional(statusBar.valueProperty());
        colors.add(statusBar, 3, 2);
        
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
        
        ok.setOnAction(e -> stage.close());
        defaults.setOnAction(e -> appearanceConstants.setConstants(AppearanceConstants.defaultConstants()));
        cancel.setOnAction(e -> {
            appearanceConstants.setConstants(previous);
            stage.close();
        });

        Scene scene = new Scene(root);

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setTitle("Appearance Settings");
    }
    
    public void showWindow() {
        previous.setConstants(appearanceConstants);
        stage.showAndWait();
    }
    
    public AppearanceConstants getConstants() {
        return appearanceConstants;
    }
}