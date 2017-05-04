package minesweeper.model;

import java.io.Serializable;

public class GameConstants implements Serializable {
    
    public final int rows;
    public final int columns;
    public final int mines;
    
    public GameConstants(Integer rows, Integer columns, Integer mines) {
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