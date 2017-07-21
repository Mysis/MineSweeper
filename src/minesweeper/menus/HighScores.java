package minesweeper.menus;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import minesweeper.util.*;

public class HighScores {

    //save file location
    File save = new File("scores.ser");

    HighScoresSave scores;

    CheckBox limitHighScoreListSize;
    TextField limit;
    Stage stage;

    public HighScores() {

        //load save if exists
        if (save.exists()) {
            try {
                scores = (HighScoresSave) MineSweeperFiles.readSerializedFile(save);
                scores.loadFromSave();
            } catch (InvalidClassException e) {
                e.printStackTrace();
                save.delete();
                scores = new HighScoresSave(5);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                scores = new HighScoresSave(5);
            }
        } else {
            scores = new HighScoresSave(5);
        }

        //init layout
        VBox root = new VBox(15);
        HBox content = new HBox(15);
        VBox left = new VBox(15);
        VBox right = new VBox(15);
        HBox buttons = new HBox(15);
        content.getChildren().addAll(left, right);
        root.getChildren().addAll(content, buttons);
        root.setPadding(new Insets(20));

        //init listview
        ListView<Long> scoresView = new ListView<>();
        scoresView.setPrefHeight(122);
        scoresView.setPrefWidth(100);
        scoresView.setSelectionModel(new DisabledMultipleSelectionModel<>());

        //convert raw longs to readable strings (MM:SS.LLL)
        scoresView.setCellFactory(new Callback<ListView<Long>, ListCell<Long>>() {
            @Override
            public ListCell<Long> call(ListView<Long> list) {
                return new ListCell<Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item == null ? "" : TimeConverter.convertTimeMillisToString(item));
                    }
                };
            }
        });

        //init reset button
        Button reset = new Button("Reset");
        reset.setPrefWidth(75);
        
        left.getChildren().addAll(scoresView);

        //create choice box with all game types except custom
        ChoiceBox<GameSettings.Type> highScoreType = new ChoiceBox<>();
        highScoreType.setConverter(new GameTypeConverter());
        for (GameSettings.Type type : GameSettings.Type.values()) {
            if (type != GameSettings.Type.CUSTOM) {
                highScoreType.getItems().add(type);
            }
        }
        highScoreType.setValue(scores.getLastTypeInHighScoresWindow());

        //create option to limit length of high score list
        limitHighScoreListSize = new CheckBox("Limit size of high score list to:");
        limitHighScoreListSize.setSelected(scores.getHasMax());
        limit = new TextField(String.valueOf(scores.getMaxSize()));
        limit.setPrefColumnCount(2);
        limit.setTextFormatter(new TextFormatter<>(new NumberFilter(2)));
        limit.disableProperty().bind(limitHighScoreListSize.selectedProperty().not());
        HBox highScoreListSizeOption = new HBox(10);
        highScoreListSizeOption.getChildren().addAll(limitHighScoreListSize, limit);
        highScoreListSizeOption.setAlignment(Pos.CENTER_LEFT);
        
        right.getChildren().addAll(highScoreType, reset, highScoreListSizeOption);

        //init buttons
        Button ok = new Button("OK");
        ok.setPrefWidth(75);
        ok.setDefaultButton(true);
        Button cancel = new Button("Cancel");
        cancel.setPrefWidth(75);
        cancel.setCancelButton(true);
        Button apply = new Button("Apply");
        apply.setPrefWidth(75);
        buttons.getChildren().addAll(ok, cancel, apply);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        
        //set reset button action
        reset.setOnAction((ActionEvent e) -> {
            //confirmation dialog if user has not suppressed dialog
            if (!scores.getSuppressResetConfimation()) {
                //create custom alert with option to suppress future confirmations
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().applyCss();
                Node graphic = alert.getDialogPane().getGraphic();
                //opt out option
                CheckBox optOut = new CheckBox();
                alert.setDialogPane(new DialogPane() {
                    @Override
                    protected Node createDetailsButton() {
                        optOut.setText("Do show this message again");
                        return optOut;
                    }
                });
                //create alert content
                alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                alert.getDialogPane().setContentText(
                        "This will permanently delete the scores of this difficulty level. This cannot be undone. Are you sure you want to continue?");
                alert.getDialogPane().setExpandableContent(new Group());
                alert.getDialogPane().setExpanded(true);
                alert.getDialogPane().setGraphic(graphic);
                alert.setTitle("Reset High Scores");
                alert.setHeaderText(null);
                Optional<ButtonType> result = alert.showAndWait();
                //clear high scores and save suppress option if 'ok' is selected
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    scores.resetScoresOfType(highScoreType.getValue());
                    try {
                        applySettings();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    scores.setSuppressResetConfirmation(optOut.isSelected());
                }
            } else {
                scores.resetScoresOfType(highScoreType.getValue());
                try {
                    applySettings();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

        //create binding for content of listview based on selection of choice box
        ObjectBinding<ObservableList<Long>> scoresListViewBinding = new ObjectBinding<ObservableList<Long>>() {
            private final List<GameSettings.Type> allTypesExceptCustom;
            {
                allTypesExceptCustom = new ArrayList<>(Arrays.asList(GameSettings.Type.values()));
                allTypesExceptCustom.remove(GameSettings.Type.CUSTOM);
                super.bind(highScoreType.valueProperty());
            }
            @Override
            protected ObservableList<Long> computeValue() {
                for (GameSettings.Type type : allTypesExceptCustom) {
                    if (highScoreType.valueProperty().get() == type) {
                        return scores.getScoresOfType(type);
                    }
                }
                return null;
            }
        };
        scoresView.itemsProperty().bind(scoresListViewBinding);
        BooleanBinding scoresEmpty = new BooleanBinding() {
            {
                super.bind(scoresListViewBinding);
            }
            @Override
            protected boolean computeValue() {
                super.bind(scoresListViewBinding.get());
                return scoresListViewBinding.get().isEmpty();
            }
        };
        reset.disableProperty().bind(scoresEmpty);

        //save last type each time a new type is selected (ie choice box is the same as the last selected when user reopends window)
        highScoreType.valueProperty().addListener((observable, oldVal, newVal) -> {
            scores.setLastTypeInHighScoresWindow(newVal);
        });

        //set button actions
        ok.setOnAction(e -> {
            try {
                applySettings();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            ((Stage) ok.getScene().getWindow()).close();
        });
        cancel.setOnAction(e -> {
            ((Stage) cancel.getScene().getWindow()).close();
        });
        apply.setOnAction(e -> {
            try {
                applySettings();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        //show window
        Scene scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setTitle("High Scores");
    }

    //private converter class to convert game types to readable strings
    private class GameTypeConverter extends StringConverter<GameSettings.Type> {

        @Override
        public String toString(GameSettings.Type type) {
            return type.toString().substring(0, 1).toUpperCase() + type.toString().substring(1).toLowerCase();
        }

        @Override
        public GameSettings.Type fromString(String string) {
            for (GameSettings.Type type : GameSettings.Type.values()) {
                if (string.toUpperCase().equals(type.toString())) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Could not find string that matches GameSettings.Type.");
        }
    }

    //apply settings
    private void applySettings() throws IOException {
        if (limitHighScoreListSize.isSelected()) {
            scores.setMaxSize(Integer.parseInt(limit.getText()));
        } else {
            scores.setHasMax(false);
        }
        scores.prepForSave();
        MineSweeperFiles.writeSerializedObject(scores, save);
    }

    public void showWindow() {
        stage.show();
    }

    //save settings
    public void save() {
        try {
            scores.prepForSave();
            MineSweeperFiles.writeSerializedObject(scores, save);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addScoreIfPossible(GameSettings.Type type, Long timeMillis) {
        return scores.addScoreIfPossible(type, timeMillis);
    }

    public HighScoresSave getScores() {
        return scores;
    }
}
