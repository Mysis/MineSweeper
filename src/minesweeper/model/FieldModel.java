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
    private ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper(false);
    private ReadOnlyBooleanWrapper win = new ReadOnlyBooleanWrapper(false);
    private ReadOnlyBooleanWrapper firstCell = new ReadOnlyBooleanWrapper(true);
    
    GameConstants gameConstants;

    public FieldModel(GameConstants gameConstants) {
        this.gameConstants = gameConstants;

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
            calculateSurroundingCells();
        }
    }
    
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
    
    private void addStateListener(CellModel cell) {
        cell.stateProperty().addListener(o -> {
            if (cell.getState() == State.REVEALED) {
                if (cell.getMine()) {
                    if (firstCell.get()) {
                        relocateMine(cell);
                        if (cell.getSurrounding() == 0) {
                            revealCells(cell);
                        }
                        if (checkWin()) {
                            win();
                        }
                    } else {
                        lose();
                    }
                } else {
                    if (cell.getSurrounding() == 0) {
                        revealCells(cell);
                    }
                    if (checkWin()) {
                        win();
                    }
                }
                firstCell.set(false);
            }
        });
    }

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
    
    private void relocateMine(CellModel cell) {
        Long startTime = System.nanoTime();
        if (cell.getMine() == false) {
            throw new IllegalArgumentException("This cell does not contain a mine.");
        }
        
        List<CellModel> possibleCells = new ArrayList<>();
        for (List<CellModel> column : cells) {
            possibleCells.addAll(column);
        }
        Collections.shuffle(possibleCells);
        boolean done = false;
        CellModel newMine;
        while(!done) {
            if (!possibleCells.get(0).getMine()) {
                newMine = possibleCells.get(0);
                newMine.setMine(true);
                cell.setMine(false);
                List<CellModel> cellsToRecalculate = surroundingCells(newMine);
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
                possibleCells.remove(0);
            }
        }
    }

    public boolean checkWin() {
        if (gameOver.get()) {
            return win.get();
        }
        for (ObservableList<CellModel> column : cells()) {
            for (CellModel cell : column) {
                if (!cell.getMine() && cell.getState() == State.HIDDEN) {
                    return false;
                }
            }
        }
        return true;
    }

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
