package minesweeper.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import minesweeper.menus.GameSettings;
    
public class HighScoresSave implements Serializable {

    private boolean hasMax; //does this list have a max size
    private int maxSize;
    private transient ObservableMap<GameSettings.Type, ObservableList<Long>> scores;
    private HashMap<GameSettings.Type, List<Long>> mapToSave; //serializable map
    private GameSettings.Type lastTypeInHighScoresWindow = GameSettings.Type.BEGINNER;
    private boolean suppressResetConfirmation = false;

    //init without max
    public HighScoresSave() {
        this.hasMax = false;
        scores = FXCollections.observableHashMap();
        for (GameSettings.Type type : GameSettings.Type.values()) {
            if (type != GameSettings.Type.CUSTOM) {
                scores.put(type, FXCollections.observableArrayList());
            }
        }
    }
    //init with max
    public HighScoresSave(int maxSize) {
        this.hasMax = true;
        this.maxSize = maxSize;
        scores = FXCollections.observableHashMap();
        for (GameSettings.Type type : GameSettings.Type.values()) {
            if (type != GameSettings.Type.CUSTOM) {
                scores.put(type, FXCollections.observableArrayList());
            }
        }
    }

    //add score to high score table if possible, return true if score is added, false if score is not added
    public boolean addScoreIfPossible(GameSettings.Type type, Long score) {
        if (type != GameSettings.Type.CUSTOM) {
            ObservableList<Long> table = scores.get(type);
            for (int i = 0; i < table.size(); i++) {
                if (table.get(i) > score) {
                    table.add(i, score);
                    if (hasMax && table.size() > maxSize) {
                        table.subList(maxSize, table.size()).clear();
                    }
                    return true;
                }
            }
            if (!hasMax || table.size() < maxSize) {
                table.add(score);
                return true;
            }
        }
        return false;
    }
    
    public void resetScoresOfType(GameSettings.Type type) {
        scores.get(type).clear();
    }
    
    //prep object to be serialized
    public void prepForSave() {
        mapToSave = new HashMap<>();
        for (GameSettings.Type type : GameSettings.Type.values()) {
            if (type != GameSettings.Type.CUSTOM) {
                List<Long> table = new ArrayList<>(scores.get(type));
                mapToSave.put(type, table);
            }
        }
    }
    //return to normal state after (this) object has been serialized
    public void loadFromSave() {
        scores = new SimpleMapProperty<>(FXCollections.observableHashMap()); //create observable map
        for (GameSettings.Type type : GameSettings.Type.values()) {
            if (type != GameSettings.Type.CUSTOM) {
                //if previous scores exists, fill with previous scores, otherwise create empty list
                if (mapToSave.get(type) != null) {
                    scores.put(type, FXCollections.observableArrayList(mapToSave.get(type)));
                } else {
                    scores.put(type, FXCollections.observableArrayList());
                }
            }
        }
    }

    public ObservableMap<GameSettings.Type, ObservableList<Long>> getScores() {
        return scores;
    }
    public ObservableList<Long> getScoresOfType(GameSettings.Type type) {
        return scores.get(type);
    }
    
    public void setHasMax(boolean val) {
        hasMax = val;
        //trim list sizes if new max is less than total number of scores
        if (hasMax) {
            for (ObservableList<Long> table : scores.values()) {
                if (table.size() > maxSize) {
                    table.subList(maxSize - 1, table.size()).clear();
                }
            }
        }
    }
    public boolean getHasMax() {
        return hasMax;
    }
    
    public void setMaxSize(int val) {
        maxSize = val;
        setHasMax(true);
    }
    public int getMaxSize() {
        return maxSize;
    }
    
    public void setLastTypeInHighScoresWindow(GameSettings.Type type) {
        lastTypeInHighScoresWindow = type;
    }
    public GameSettings.Type getLastTypeInHighScoresWindow() {
        return lastTypeInHighScoresWindow;
    }
    
    public void setSuppressResetConfirmation(boolean val) {
        suppressResetConfirmation = val;
    }
    public boolean getSuppressResetConfimation() {
        return suppressResetConfirmation;
    }
}