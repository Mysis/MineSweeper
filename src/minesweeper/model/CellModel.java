package minesweeper.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CellModel {

    //number of mines surrounding cell
    private IntegerProperty surrounding = new SimpleIntegerProperty(0);

    private BooleanProperty mine; //whether or not is mine
    private ObjectProperty<State> state = new SimpleObjectProperty<>(State.HIDDEN); //current state
    private ReadOnlyBooleanProperty gameOver; //reference to game over from fieldmodel

    //enum for current state of cell
    public enum State {
        HIDDEN,
        REVEALED,
        FLAGGED
    }

    //init cell
    public CellModel(boolean isMine, ReadOnlyBooleanProperty gameOverCheck) {
        mine = new SimpleBooleanProperty(isMine);
        gameOver = gameOverCheck;
    }

    //toggle flag if game is not over and cell is not revealed
    public void toggleFlag() {
        if (!gameOver.get()) {
            if (state.get() != State.REVEALED) {
                if (state.get() == State.FLAGGED) {
                    state.set(State.HIDDEN);
                } else {
                    state.set(State.FLAGGED);
                }
            }
        }
    }

    //reveal cell if game is not over and cell is not flagged
    public void reveal() {
        if (!gameOver.get()) {
            if (state.get() != State.FLAGGED) {
                state.set(State.REVEALED);
            }
        }
    }

    public IntegerProperty surroundingProperty() {
        return surrounding;
    }
    public void setSurrounding(int val) {
        surrounding.set(val);
    }
    public int getSurrounding() {
        return surrounding.get();
    }

    public ObjectProperty<State> stateProperty() {
        return state;
    }
    public void setState(State newState) {
        state.set(newState);
    }
    public State getState() {
        return state.get();
    }

    public BooleanProperty mineProperty() {
        return mine;
    }
    public void setMine(boolean isMine) {
        mine.set(isMine);
    }
    public boolean getMine() {
        return mine.get();
    }
}
