package minesweeper.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

public class DisabledMultipleSelectionModel<T> extends MultipleSelectionModel<T> {
    {
    super.setSelectedIndex(-1);
    super.setSelectedItem(null);
    }
    @Override
    public ObservableList<Integer> getSelectedIndices() { return FXCollections.emptyObservableList() ; }
    @Override
    public ObservableList<T> getSelectedItems() { return FXCollections.emptyObservableList() ; }
    @Override
    public void selectAll() {}
    @Override
    public void selectFirst() {}
    @Override
    public void selectIndices(int index, int... indicies) {}
    @Override
    public void selectLast() {}
    @Override
    public void clearAndSelect(int index) {}
    @Override
    public void clearSelection() {}
    @Override
    public void clearSelection(int index) {}
    @Override
    public boolean isEmpty() { return true ; }
    @Override
    public boolean isSelected(int index) { return false ; }
    @Override
    public void select(int index) {}
    @Override
    public void select(T item) {}
    @Override
    public void selectNext() {}
    @Override
    public void selectPrevious() {}
}
