package minesweeper.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FieldModel {

    private ObservableList<ObservableList<CellModel>> cells = FXCollections.observableArrayList();
    private ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper(false);
    private ReadOnlyBooleanWrapper win = new ReadOnlyBooleanWrapper(false);

    public FieldModel(int rows, int columns, int mines) {

        List<Boolean> minesBag = new ArrayList<>();
        for (int i = 0; i < rows * columns; i++) {
            if (mines > 0) {
                minesBag.add(true);
                mines--;
            } else {
                minesBag.add(false);
            }
        }
        Collections.shuffle(minesBag);
        for (int i = 0; i < columns; i++) {
            cells.add(FXCollections.observableArrayList());
            for (int j = 0; j < rows; j++) {
                CellModel newCell = new CellModel(minesBag.get(0), gameOver.getReadOnlyProperty());
                minesBag.remove(0);
                if (newCell.getMine()) {
                    newCell.stateProperty().addListener((o) -> {
                        if (newCell.getState() == CellModel.State.REVEALED) {
                            lose();
                        }
                    });
                } else {
                    newCell.stateProperty().addListener((o) -> {
                        if (newCell.getState() == CellModel.State.REVEALED) {
                            if (newCell.getSurrounding() == 0) {
                                revealCells(newCell);
                            }
                            if (checkWin()) {
                                win();
                            }
                        }
                    });
                }
                cells.get(i).add(newCell);
            }
        }

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
            throw new IllegalArgumentException("cell does not exist in this grid");
        }

        List<CellModel> surrounding = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    try {
                        surrounding.add(cells.get(x + i).get(y + j));
                    } catch (IndexOutOfBoundsException e) {
                    }
                }
            }
        }

        return surrounding;
    }

    public void revealCells(CellModel cell) {
        for (CellModel surrounding : surroundingCells(cell)) {
            if (surrounding.getState() != CellModel.State.REVEALED) {
                surrounding.reveal();
                if (surrounding.getSurrounding() == 0) {
                    revealCells(surrounding);
                }
            }
        }
    }

    public boolean checkWin() {
        if (gameOver.get()) {
            return false;
        }
        for (ObservableList<CellModel> column : cells()) {
            for (CellModel cell : column) {
                if (!cell.getMine() && cell.getState() == CellModel.State.HIDDEN) {
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
                    cell.setState(CellModel.State.FLAGGED);
                }
            }
        }
        win.set(true);
        gameOver.set(true);
    }

    public void lose() {
        for (ObservableList<CellModel> column : cells()) {
            for (CellModel cell : column) {
                if (cell.getMine() && cell.getState() != CellModel.State.REVEALED) {
                    cell.reveal();
                }
            }
        }
        gameOver.set(true);
    }

    public ObservableList<ObservableList<CellModel>> cells() {
        return cells;
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
