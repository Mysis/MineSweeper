package minesweeper.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import minesweeper.model.GameConstants;

public class AppearanceValues {
    
    //currently, size options are not configurable, possibly in a later update?
    private final DoubleProperty gameBoundarySize = new SimpleDoubleProperty();
    private final DoubleProperty cellStartSize = new SimpleDoubleProperty();

    private final ObjectProperty<Color> cellColor = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> cellRevealedColor = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> mineColor = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> flagColor = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> statusBarColor = new SimpleObjectProperty<>();
    
    //object that holds all current colors for the game
    public AppearanceValues(double gameBoundarySize, double cellStartSize, Color cellColor, Color cellRevealedColor, Color mineColor, Color flagColor, Color backgroundColor, Color statusBarColor) {
        setValues(gameBoundarySize, cellStartSize, cellColor, cellRevealedColor, mineColor, flagColor, backgroundColor, statusBarColor);
    }
    
    public AppearanceValues(AppearanceValues constants) {
        setValues(constants);
    }
    
    //change all values
    public void setValues(double gameBoundarySize, double cellStartSize, Color cellColor, Color cellRevealedColor, Color mineColor, Color flagColor, Color backgroundColor, Color statusBarColor) {
        setGameBoundarySize(gameBoundarySize);
        setCellStartSize(cellStartSize);
        setCellColor(cellColor);
        setCellRevealedColor(cellRevealedColor);
        setMineColor(mineColor);
        setFlagColor(flagColor);
        setBackgroundColor(backgroundColor);
        setStatusBarColor(statusBarColor);
    }
    
    public void setValues(AppearanceValues constants) {
        setGameBoundarySize(constants.getGameBoundarySize());
        setCellStartSize(constants.getCellStartSize());
        setCellColor(constants.getCellColor());
        setCellRevealedColor(constants.getCellRevealedColor());
        setMineColor(constants.getMineColor());
        setFlagColor(constants.getFlagColor());
        setBackgroundColor(constants.getBackgroundColor());
        setStatusBarColor(constants.getStatusBarColor());
    }
    
    //calculate how big the field should be with current sizes
    public static double[] calculateFieldStartSize(GameConstants gameConstants, AppearanceValues appearanceValues) {
        double[] size = new double[2];
        size[0] = gameConstants.columns * appearanceValues.getCellStartSize() + gameConstants.columns + appearanceValues.getGameBoundarySize() * 2;
        size[1] = gameConstants.rows * appearanceValues.getCellStartSize() + gameConstants.rows + appearanceValues.getGameBoundarySize() * 2;
        return size;
    }

    public static AppearanceValues defaultValues() {
        return new AppearanceValues(20, 25, Color.GREY, Color.LIGHTGREY, Color.BLACK, Color.RED, Color.DARKRED, Color.WHITE);
    }
    
    public DoubleProperty gameBoundarySizeProperty() {
        return gameBoundarySize;
    }
    public double getGameBoundarySize() {
        return gameBoundarySize.get();
    }
    public void setGameBoundarySize(double val) {
        gameBoundarySize.set(val);
    }
    
    public DoubleProperty cellStartSizeProperty() {
        return cellStartSize;
    }
    public double getCellStartSize() {
        return cellStartSize.get();
    }
    public void setCellStartSize(double val) {
        cellStartSize.set(val);
    }
    
    public ObjectProperty<Color> cellColorProperty() {
        return cellColor;
    }
    public Color getCellColor() {
        return cellColor.get();
    }
    public void setCellColor(Color val) {
        cellColor.set(val);
    }
    
    public ObjectProperty<Color> cellRevealedColorProperty() {
        return cellRevealedColor;
    }
    public Color getCellRevealedColor() {
        return cellRevealedColor.get();
    }
    public void setCellRevealedColor(Color val) {
        cellRevealedColor.set(val);
    }
    
    public ObjectProperty<Color> mineColorProperty() {
        return mineColor;
    }
    public Color getMineColor() {
        return mineColor.get();
    }
    public void setMineColor(Color val) {
        mineColor.set(val);
    }
    
    public ObjectProperty<Color> flagColorProperty() {
        return flagColor;
    }
    public Color getFlagColor() {
        return flagColor.get();
    }
    public void setFlagColor(Color val) {
        flagColor.set(val);
    }
    
    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }
    public Color getBackgroundColor() {
        return backgroundColor.get();
    }
    public void setBackgroundColor(Color val) {
        backgroundColor.set(val);
    }
    
    public ObjectProperty<Color> statusBarColorProperty() {
        return statusBarColor;
    }
    public Color getStatusBarColor() {
        return statusBarColor.get();
    }
    public void setStatusBarColor(Color val) {
        statusBarColor.set(val);
    }
}
