package minesweeper.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import minesweeper.model.GameConstants;

public class AppearanceConstants {
    
    private final DoubleProperty gameBoundarySize;
    private final DoubleProperty cellStartSize;

    private final ObjectProperty<Color> cellColor;
    private final ObjectProperty<Color> cellRevealedColor;
    private final ObjectProperty<Color> mineColor;
    private final ObjectProperty<Color> flagColor;
    private final ObjectProperty<Color> backgroundColor;
    private final ObjectProperty<Color> statusBarColor;
    
    public AppearanceConstants(double gameBoundarySize, double cellStartSize, Color cellColor, Color cellRevealedColor, Color mineColor, Color flagColor, Color backgroundColor, Color statusBarColor) {
        this.gameBoundarySize = new SimpleDoubleProperty(gameBoundarySize);
        this.cellStartSize = new SimpleDoubleProperty(cellStartSize);
        this.cellColor = new SimpleObjectProperty<>(cellColor);
        this.cellRevealedColor = new SimpleObjectProperty<>(cellRevealedColor);
        this.mineColor = new SimpleObjectProperty<>(mineColor);
        this.flagColor = new SimpleObjectProperty<>(flagColor);
        this.backgroundColor = new SimpleObjectProperty<>(backgroundColor);
        this.statusBarColor = new SimpleObjectProperty<>(statusBarColor);
    }
    
    public AppearanceConstants(AppearanceConstants constants) {
        gameBoundarySize = new SimpleDoubleProperty();
        cellStartSize = new SimpleDoubleProperty();
        cellColor = new SimpleObjectProperty<>();
        cellRevealedColor = new SimpleObjectProperty<>();
        mineColor = new SimpleObjectProperty<>();
        flagColor = new SimpleObjectProperty<>();
        backgroundColor = new SimpleObjectProperty<>();
        statusBarColor = new SimpleObjectProperty<>();
        setConstants(constants);
    }
    
    public void setConstants(AppearanceConstants constants) {
        setGameBoundarySize(constants.getGameBoundarySize());
        setCellStartSize(constants.getCellStartSize());
        setCellColor(constants.getCellColor());
        setCellRevealedColor(constants.getCellRevealedColor());
        setMineColor(constants.getMineColor());
        setFlagColor(constants.getFlagColor());
        setBackgroundColor(constants.getBackgroundColor());
        setStatusBarColor(constants.getStatusBarColor());
    }
    
    public static double[] calculateFieldStartSize(GameConstants gameConstants, AppearanceConstants appearanceConstants) {
        double[] size = new double[2];
        size[0] = gameConstants.columns * appearanceConstants.getCellStartSize() + gameConstants.columns + appearanceConstants.getGameBoundarySize() * 2;
        size[1] = gameConstants.rows * appearanceConstants.getCellStartSize() + gameConstants.rows + appearanceConstants.getGameBoundarySize() * 2;
        return size;
    }
    
    public static AppearanceConstants defaultConstants() {
        return new AppearanceConstants(20, 25, Color.GREY, Color.LIGHTGREY, Color.BLACK, Color.RED, Color.DARKRED, Color.WHITE);
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
