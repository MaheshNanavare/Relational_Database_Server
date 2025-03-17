package edu.uob.dataclasses;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<String> values;


    // Constructor used in table class method addRow(values - list of Strings)
    public Row(List<String> values) {
        this.values = new ArrayList<>(values);
    }

    public List<String> getValues() {
        return values;
    }

    // Update requires this method
    public void updateValue(int columnIndex, String newValue) {
        if (columnIndex < 0 || columnIndex >= values.size()) {
            throw new IndexOutOfBoundsException("Invalid column index.");
        }
        values.set(columnIndex, newValue);
    }

    // Used for all rows using loop in deleteColumn(String columnName) method of table class
    public void deleteValue(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= values.size()) {
            throw new IndexOutOfBoundsException("Invalid column index.");
        }
        values.remove(columnIndex);
    }

    @Override
    // Need to save row in .tab file
    public String toString() {
        return String.join("\t", values);
    }
}
