package minesweeper;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class Menus extends MenuBar {
    
    public Menus() {
        
        Menu gameMenu = new Menu("Game");
        
        getMenus().add(gameMenu);
    }
}