package minesweeper.menus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import minesweeper.view.AppearanceValues;

public class AppearanceSettingsSave implements Serializable {
    
    public List<Double> sizes;
    public List<String> colors;
    public List<List<String>> customColors;
    
    public AppearanceSettingsSave(AppearanceValues appearance, List<ObservableList<Color>> customColorValues) {
        
        sizes = new ArrayList<>();
        Collections.addAll(sizes,
                appearance.getGameBoundarySize(),
                appearance.getCellStartSize());
        
        List<Color> colorValues = new ArrayList<>();
        Collections.addAll(colorValues, 
                appearance.getCellColor(),
                appearance.getCellRevealedColor(), 
                appearance.getMineColor(), 
                appearance.getFlagColor(), 
                appearance.getBackgroundColor(), 
                appearance.getStatusBarColor());
        colors = new ArrayList<>();
        for (Color color : colorValues) {
            colors.add(color.toString());
        }
        
        customColors = new ArrayList<>();
        for (ObservableList<Color> customColorSet : customColorValues) {
            List<String> set = new ArrayList<>();
            for (Color color : customColorSet) {
                set.add(color.toString());
            }
            customColors.add(set);
        }
    }
    
    public AppearanceValues createAppearanceValuesFromSave() {
        return new AppearanceValues(
                sizes.get(0), 
                sizes.get(1),
                Color.valueOf(colors.get(0)),
                Color.valueOf(colors.get(1)),
                Color.valueOf(colors.get(2)),
                Color.valueOf(colors.get(3)),
                Color.valueOf(colors.get(4)),
                Color.valueOf(colors.get(5))
                );
    }
}