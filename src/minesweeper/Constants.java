package minesweeper;

import javafx.scene.paint.Color;

public final class Constants {
    
    public static int ROWS = 9;
    public static int COLUMNS = 9;
    public static int MINES = 10;
    
    public static double CELL_START_SIZE = 40;
    public static double FIELD_START_WIDTH = Constants.COLUMNS * Constants.CELL_START_SIZE + (Constants.COLUMNS);
    public static double FIELD_START_HEIGHT = Constants.ROWS * Constants.CELL_START_SIZE + (Constants.ROWS);
    
    public static Color CELL_COLOR = Color.GREY;
    public static Color CELL_REVEALED_COLOR = Color.LIGHTGRAY;
    public static Color MINE_COLOR = Color.BLACK;
    public static Color FLAG_COLOR = Color.RED;
    
    public static Color BACKGROUND_COLOR = Color.BROWN;
    public static double GAME_BOUNDARY_SIZE = 20;
}
