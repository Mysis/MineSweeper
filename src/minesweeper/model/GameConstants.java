package minesweeper.model;

public class GameConstants {
    
    public final int rows;
    public final int columns;
    public final int mines;
    
    public GameConstants(int rows, int columns, int mines) {
        if (rows * columns >= mines) {
            this.rows = rows;
            this.columns = columns;
            this.mines = mines;
        } else {
            throw new ArithmeticException("The board isn't big enough for all the mines");
        }
    }
    
    @Override
    public String toString() {
        return new String("rows: " + rows + ", columns: " + columns + ", mines: " + mines);
    }
}