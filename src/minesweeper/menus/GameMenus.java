package minesweeper.menus;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class GameMenus extends MenuBar {
    
    Menu gameMenu = new Menu("Game");
    MenuItem newGameItem = new MenuItem("New Game");
    MenuItem optionsItem = new MenuItem("Options");
    MenuItem changeAppearanceItem = new MenuItem("Change Appearance");
    MenuItem highScoresItem = new MenuItem("High Scores");
    MenuItem exitItem = new MenuItem("Exit");
    
    public GameMenus() {
        newGameItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        optionsItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        changeAppearanceItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
        highScoresItem.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN));
        exitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        gameMenu.getItems().addAll(newGameItem, optionsItem, changeAppearanceItem, highScoresItem, exitItem);
        getMenus().add(gameMenu);
    }
    
    public MenuItem newGameItem() {
        return newGameItem;
    }
    public MenuItem optionsItem() {
        return optionsItem;
    }
    public MenuItem changeAppearanceItem() {
        return changeAppearanceItem;
    }
    public MenuItem highScoresItem() {
        return highScoresItem;
    }
    public MenuItem exitItem() {
        return exitItem;
    }
}