package minesweeper;

import javafx.scene.paint.Color;
import minesweeper.model.GameConstants;

public final class Constants {
    
    public static final int DEFAULT_ROWS = 9;
    public static final int DEFAULT_COLUMNS = 9;
    public static final int DEFAULT_MINES = 10;
    
    public static final double GAME_BOUNDARY_SIZE = 20;
    public static final double CELL_START_SIZE = 35;
    
    public static Color CELL_COLOR = Color.GREY;
    public static Color CELL_REVEALED_COLOR = Color.LIGHTGRAY;
    public static Color MINE_COLOR = Color.BLACK;
    public static Color FLAG_COLOR = Color.RED;
    public static Color BACKGROUND_COLOR = Color.BROWN;
    
    public static double[] calculateFieldStartSize(GameConstants constants) {
        double[] size = new double[2];
        size[0] = constants.columns * CELL_START_SIZE + constants.columns + GAME_BOUNDARY_SIZE * 2;
        size[1] = constants.rows * CELL_START_SIZE + constants.rows + GAME_BOUNDARY_SIZE * 2;
        return size;
    }
}
