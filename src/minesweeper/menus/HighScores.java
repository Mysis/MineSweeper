package minesweeper.menus;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import minesweeper.util.HighScoresSave;
import minesweeper.util.MineSweeperFiles;
import minesweeper.util.NumberFilter;
import minesweeper.view.StatusBar;

public class HighScores {

    File save = new File("scores.ser");

    HighScoresSave scores;

    CheckBox limitHighScoreListSize;
    TextField limit;
    Stage stage;

    public HighScores() {

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

        VBox root = new VBox(15);
        HBox content = new HBox(15);
        VBox left = new VBox(15);
        VBox right = new VBox(15);
        HBox buttons = new HBox(15);
        content.getChildren().addAll(left, right);
        root.getChildren().addAll(content, buttons);
        root.setPadding(new Insets(20));

        ListView<Long> scoresView = new ListView<>();
        scoresView.setFocusModel(null);
        scoresView.setPrefHeight(122);
        scoresView.setPrefWidth(100);

        scoresView.setCellFactory(new Callback<ListView<Long>, ListCell<Long>>() {
            @Override
            public ListCell<Long> call(ListView<Long> list) {
                return new ListCell<Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item == null ? "" : StatusBar.convertTimeMillisToString(item));
                    }
                };
            }
        });

        Button reset = new Button("Reset");
        reset.setPrefWidth(75);
        
        left.getChildren().addAll(scoresView);

        ChoiceBox<GameSettings.Type> highScoreType = new ChoiceBox<>();
        highScoreType.setConverter(new GameTypeConverter());
        for (GameSettings.Type type : GameSettings.Type.values()) {
            if (type != GameSettings.Type.CUSTOM) {
                highScoreType.getItems().add(type);
            }
        }
        highScoreType.setValue(scores.getLastTypeInHighScoresWindow());

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
        
        reset.setOnAction(e -> {
            if (!scores.getSuppressResetConfimation()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().applyCss();
                Node graphic = alert.getDialogPane().getGraphic();
                CheckBox optOut = new CheckBox();
                alert.setDialogPane(new DialogPane() {
                    @Override
                    protected Node createDetailsButton() {
                        optOut.setText("Do show this message again");
                        return optOut;
                    }
                });
                alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                alert.getDialogPane().setContentText(
                        "This will permanently delete the scores of this difficulty level. This cannot be undone. Are you sure you want to continue?");
                alert.getDialogPane().setExpandableContent(new Group());
                alert.getDialogPane().setExpanded(true);
                alert.getDialogPane().setGraphic(graphic);
                alert.setTitle("Reset High Scores");
                alert.setHeaderText(null);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    scores.resetScoresOfType(highScoreType.getValue());
                    scores.setSuppressResetConfirmation(optOut.isSelected());
                }
            } else {
                scores.resetScoresOfType(highScoreType.getValue());
            }
        });

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

        highScoreType.valueProperty().addListener((observable, oldVal, newVal) -> {
            scores.setLastTypeInHighScoresWindow(newVal);
        });

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

        Scene scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setTitle("High Scores");
    }

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
