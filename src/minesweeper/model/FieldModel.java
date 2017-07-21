package minesweeper.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import minesweeper.model.CellModel.State;

public class FieldModel {

    private ObservableList<ObservableList<CellModel>> cells = FXCollections.observableArrayList();
    private ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper(false); //is game over
    private ReadOnlyBooleanWrapper win = new ReadOnlyBooleanWrapper(false); //did player win
    private ReadOnlyBooleanWrapper firstCell = new ReadOnlyBooleanWrapper(true); //has the player started the game (true if player has not started)
    
    GameConstants gameConstants;

    public FieldModel(GameConstants gameConstants) {
        this.gameConstants = gameConstants;

        //shuffle mines into a bag, then select them one by one out of the bag (like a bag of marbles)
        List<Boolean> minesBag = new ArrayList<>();
        int addMines = gameConstants.mines;
        for (int i = 0; i < gameConstants.rows * gameConstants.columns; i++) {
            if (addMines > 0) {
                minesBag.add(true);
                addMines--;
            } else {
                minesBag.add(false);
            }
        }
        Collections.shuffle(minesBag);
        for (int i = 0; i < gameConstants.columns; i++) {
            cells.add(FXCollections.observableArrayList());
            for (int j = 0; j < gameConstants.rows; j++) {
                CellModel newCell = new CellModel(minesBag.get(0), gameOver.getReadOnlyProperty());
                minesBag.remove(0);
                addStateListener(newCell);
                cells.get(i).add(newCell);
            }
        }
        calculateSurroundingCells();
    }
    
    //assign surrounding number of mines to all cells
    private void calculateSurroundingCells() {
        for (List<CellModel> column : cells) {
            for (CellModel cell : column) {
                int surroundingMines = 0;
                for (CellModel surrounding : surroundingCells(cell)) {
                    if (surrounding.getMine()) {
                        surroundingMines++;
                    }
                }
                cell.setSurrounding(surroundingMines);
            }
        }
    }

    //get cells surrounding passed in cell
    private List<CellModel> surroundingCells(CellModel cell) throws IllegalArgumentException {
        int x = -2;
        int y = -2;
        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).indexOf(cell) != -1) {
                x = i;
                y = cells.get(i).indexOf(cell);
            }
        }

        if (x == -2 || y == -2) {
            throw new IllegalArgumentException("Cell does not exist in this grid.");
        }

        //get all cells surrounding passed in cell, excluding itself and ignoring array out of bounds errors (if cell is on the edge of the grid)
        List<CellModel> surrounding = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    try {
                        surrounding.add(cells.get(x + i).get(y + j));
                    } catch (IndexOutOfBoundsException e) {}
                }
            }
        }

        return surrounding;
    }
    
    //add listeners to cell
    private void addStateListener(CellModel cell) {
        cell.stateProperty().addListener(o -> {
            //called each time cell is revealed
            if (cell.getState() == State.REVEALED) {
                if (cell.getMine()) {
                    //if the first cell clicked is a mine, relocate the mine
                    if (firstCell.get()) {
                        relocateMine(cell);
                        if (cell.getSurrounding() == 0) {
                            revealCells(cell);
                        }
                    } else {
                        lose();
                    }
                } else {
                    //reveal all cells surrounding selected cell if cell has no mines surrounding it
                    if (cell.getSurrounding() == 0) {
                        revealCells(cell);
                    }
                }
                firstCell.set(false);
                if (checkWin()) {
                    win();
                }
            }
        });
    }

    //reveal cells surrounding passed in cell
    public void revealCells(CellModel cell) {
        for (CellModel surrounding : surroundingCells(cell)) {
            if (surrounding.getState() == State.HIDDEN) {
                surrounding.reveal();
                if (surrounding.getSurrounding() == 0) {
                    revealCells(surrounding);
                }
            }
        }
    }
    
    //relocate mine if mine is the first cell clicked
    private void relocateMine(CellModel cell) throws IllegalArgumentException {
        if (cell.getMine() == false) {
            throw new IllegalArgumentException("This cell does not contain a mine.");
        }
        
        //get all cells, then randomly select cells until a cell without a mine is selected
        List<CellModel> possibleCells = new ArrayList<>();
        for (List<CellModel> column : cells) {
            possibleCells.addAll(column);
        }
        Collections.shuffle(possibleCells);
        boolean done = false;
        CellModel newMine;
        while(!done) {
            //continue only if cell does not contain a mine
            if (!possibleCells.get(0).getMine()) {
                newMine = possibleCells.get(0); //cell where the new mine will be placed
                newMine.setMine(true);
                cell.setMine(false);
                List<CellModel> cellsToRecalculate = surroundingCells(newMine); //cells that need to have surrounding mines recalculated after move, start with cells around new mine
                for (CellModel cellToRecalculate : cellsToRecalculate) {
                    int surrounding = 0;
                    for (CellModel cellSurroundingCellToRecalculate : surroundingCells(cellToRecalculate)) {
                        if (cellSurroundingCellToRecalculate.getMine()) {
                            surrounding++;
                        }
                    }
                    cellToRecalculate.setSurrounding(surrounding);
                }
                cellsToRecalculate = surroundingCells(cell); //recalculate cells surrounding old mine
                for (CellModel cellToRecalculate : cellsToRecalculate) {
                    int surrounding = 0;
                    for (CellModel cellSurroundingCellToRecalculate : surroundingCells(cellToRecalculate)) {
                        if (cellSurroundingCellToRecalculate.getMine()) {
                            surrounding++;
                        }
                    }
                    cellToRecalculate.setSurrounding(surrounding);
                }
                done = true;
            } else {
                //remove cell from the bag if it is a mine
                possibleCells.remove(0);
            }
        }
    }

    //check if player has won
    public boolean checkWin() {
        if (gameOver.get()) {
            return win.get();
        }
        for (ObservableList<CellModel> column : cells()) {
            for (CellModel cell : column) {
                //if any cells contain a hidden mine, return false (if the player has revealed all cells that do not contain a mine, will return true, even if some mines are still hidden)
                if (!cell.getMine() && cell.getState() == State.HIDDEN) {
                    return false;
                }
            }
        }
        return true;
    }

    //flag mines that have not been flagged
    public void win() {
        for (ObservableList<CellModel> column : cells()) {
            for (CellModel cell : column) {
                if (cell.getMine()) {
                    cell.setState(State.FLAGGED);
                }
            }
        }
        win.set(true);
        gameOver.set(true);
    }

    //reveal all mines
    public void lose() {
        for (ObservableList<CellModel> column : cells()) {
            for (CellModel cell : column) {
                if (cell.getMine() && cell.getState() != State.REVEALED) {
                    cell.reveal();
                }
            }
        }
        gameOver.set(true);
    }

    public ObservableList<ObservableList<CellModel>> cells() {
        return cells;
    }
    
    //binding that updates based on the number of flags that have been placed by the user
    public IntegerBinding flagsPlacedProperty() {
        return new IntegerBinding() {
            List<CellModel> allCells = new ArrayList<>();
            {
                for (ObservableList<CellModel> column : cells()) {
                    for (CellModel cell : column) {
                        allCells.add(cell);
                        super.bind(cell.stateProperty());
                    }
                }
            }
            @Override
            protected int computeValue() {
                int value = 0;
                for (CellModel cell : allCells) {
                    if (cell.stateProperty().get() == State.FLAGGED) {
                        value += 1;
                    }
                }
                return value;
            }
        };
    }
    
    public GameConstants getGameConstants() {
        return gameConstants;
    }
    
    public ReadOnlyBooleanProperty firstCellProperty() {
        return firstCell.getReadOnlyProperty();
    }
    public boolean getFirstCell() {
        return firstCell.get();
    }

    public ReadOnlyBooleanProperty gameOverProperty() {
        return gameOver.getReadOnlyProperty();
    }
    public boolean getGameOver() {
        return gameOver.get();
    }

    public ReadOnlyBooleanProperty winProperty() {
        return win.getReadOnlyProperty();
    }
    public boolean getWin() {
        return win.get();
    }
}
