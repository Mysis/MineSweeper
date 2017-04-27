package minesweeper.view;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import minesweeper.model.FieldModel;

public class StatusBar extends BorderPane {
    
    FieldModel model;
    
    StopWatch timer;
    ChangeListener<Boolean> timeStartListener;
    ChangeListener<Boolean> gameOverListener;
    
    Label flagsLeftLabel;
    
    public StatusBar(AppearanceConstants appearanceConstants) {
        
        timer = new StopWatch();
        timeStartListener = (observable, oldVal, newVal) -> {
            if (!newVal) {
                timer.start();
            }
        };
        gameOverListener = (observable, oldVal, newVal) -> {
            if (newVal) {
                timer.stop();
            }
        };
        
        StringBinding timeString = new StringBinding() {
            {
                super.bind(timer.timeMillisProperty());
            }
            @Override
            protected String computeValue() {
                Long time = timer.timeMillisProperty().get();
                if (time >= 3600000) {
                    return String.format("%1$tH:%1$tM:%1$tS.%1$tL", time);
                } else if (time >= 60000) {
                    return String.format("%1$tM:%1$tS.%1$tL", time);
                } else {
                    return String.format("%1$tS.%1$tL", time);
                }
            }
        };
        
        Label timeLeftLabel = new Label();
        timeLeftLabel.textProperty().bind(Bindings.concat("Time: ", timeString));
        ObjectBinding<Color> textColor = new ObjectBinding<Color>() {
            {
                super.bind(appearanceConstants.statusBarColorProperty());
            }
            @Override
            protected Color computeValue() {
                Color backColor = appearanceConstants.statusBarColorProperty().get();
                double colorValue = 1 - (.299 * backColor.getRed() + .597 * backColor.getGreen() + .114 * backColor.getBlue());
                if (colorValue < 0.5) {
                    return Color.BLACK;
                } else {
                    return Color.WHITE;
                }
            }
        };
        timeLeftLabel.textFillProperty().bind(textColor);
        
        Polygon flagIcon = new Polygon(
                6.25, 6.25,
                18.75, 12.5,
                6.25, 18.75
        );
        flagIcon.fillProperty().bind(appearanceConstants.flagColorProperty());
        flagsLeftLabel = new Label();
        flagsLeftLabel.textFillProperty().bind(textColor);
        flagsLeftLabel.setAlignment(Pos.CENTER_RIGHT);
        
        GridPane flagBox = new GridPane();
        flagBox.add(flagIcon, 0, 0);
        flagBox.setValignment(flagIcon, VPos.CENTER);
        flagBox.setHalignment(flagIcon, HPos.LEFT);
        flagBox.add(flagsLeftLabel, 1, 0);
        flagBox.setHgap(5);
        
        setLeft(timeLeftLabel);
        setRight(flagBox);
        
        backgroundProperty().bind(Bindings.createObjectBinding(() -> new Background(new BackgroundFill(appearanceConstants.statusBarColorProperty().get(), CornerRadii.EMPTY, Insets.EMPTY)), appearanceConstants.statusBarColorProperty()));
        setPadding(new Insets(5, 20, 5, 20));
    }
    
    private class StopWatch extends AnimationTimer {
        private boolean start = false;
        private long startTime;
        private ReadOnlyLongWrapper timeMillis = new ReadOnlyLongWrapper(0);
        @Override
        public void start() {
            start = true;
            super.start();
        }
        @Override
        public void handle(long timestamp) {
            if (start) {
                startTime = timestamp;
                start = false;
            }
            timeMillis.set((timestamp - startTime) / 1000000);
        }
        public void resetTime() {
            timeMillis.set(0);
        }
        public ReadOnlyLongProperty timeMillisProperty() {
            return timeMillis.getReadOnlyProperty();
        }
    }
    
    public void newGame(FieldModel model) {
        try {
            this.model.firstCellProperty().removeListener(timeStartListener);
        } catch (NullPointerException e)  {}
        try {
            this.model.firstCellProperty().removeListener(gameOverListener);
        } catch (NullPointerException e)  {}
        this.model = model;
        timer.stop();
        model.firstCellProperty().addListener(timeStartListener);
        model.gameOverProperty().addListener(gameOverListener);
        flagsLeftLabel.textProperty().bind(Bindings.concat(model.flagsPlacedProperty(), "/", model.getGameConstants().mines));
    }
    public void setModel(FieldModel model) {
        this.model = model;
    }
    
    public ReadOnlyLongProperty timeProperty() {
        return timer.timeMillisProperty();
    }
    public long getTime() {
        return timer.timeMillisProperty().get();
    }
}