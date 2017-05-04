package minesweeper.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import minesweeper.menus.GameSettings;
    
public class HighScores implements Serializable {

    private final int maxSize;
    private HashMap<GameSettings.Type, List<Long>> scores;

    public HighScores(int maxSize) {
        scores = new HashMap<>();
        this.maxSize = maxSize;
        for (GameSettings.Type type : GameSettings.Type.values()) {
            if (type != GameSettings.Type.CUSTOM) {
                scores.put(type, new ArrayList<>());
            }
        }
    }

    public boolean addScoreIfPossible(GameSettings.Type type, Long score) {
        List<Long> table = scores.get(type);
        for (int i = 0; i < table.size(); i++) {
            if (table.get(i) > score) {
                table.add(i, score);
                if (table.size() > maxSize) {
                    table.subList(maxSize, table.size()).clear();
                }
                return true;
            }
        }
        if (table.size() < maxSize) {
            table.add(score);
            return true;
        }
        return false;
    }

    public List<Long> getScores(GameSettings.Type type) {
        return scores.get(type);
    }
}