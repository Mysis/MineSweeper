package minesweeper.menus;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class GameMenus extends MenuBar {
    
    Menu gameMenu = new Menu("Game");
    MenuItem newGameItem = new MenuItem("New Game");
    MenuItem settingsItem = new MenuItem("Settings");
    MenuItem changeAppearanceItem = new MenuItem("Change Appearance");
    MenuItem exitItem = new MenuItem("Exit");
    
    public GameMenus() {
        gameMenu.getItems().addAll(newGameItem, settingsItem, changeAppearanceItem, exitItem);
        getMenus().add(gameMenu);
    }
    
    public MenuItem newGameItem() {
        return newGameItem;
    }
    public MenuItem settingsItem() {
        return settingsItem;
    }
    public MenuItem changeAppearanceItem() {
        return changeAppearanceItem;
    }
    public MenuItem exitItem() {
        return exitItem;
    }
}