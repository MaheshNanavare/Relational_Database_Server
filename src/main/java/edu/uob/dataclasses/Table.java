package edu.uob.dataclasses;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private final String name;
    private List<String> columns;
    private List<Row> rows;

    public Table(String name, List<String> columns) {
        this.name = name;
        // Setting list of columns names Strings
        this.columns = new ArrayList<>(columns);
        // Initially no rows
        this.rows = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> newRows) {
        rows = newRows;
    }

    public void addRow(List<String> values) throws IllegalArgumentException{
        if (values.size() != columns.size()) {
            throw new IllegalArgumentException("Row values does not match column count.");
        }
        rows.add(new Row(values));
    }

    public void addColumn(String columnName) throws IllegalArgumentException{
        for (String col : columns) {
            if (col.equalsIgnoreCase(columnName)) {
                throw new IllegalArgumentException();
            }
        }
        columns.add(columnName);
        // Append empty value for new column in every row
        for (Row row : rows) {
            row.getValues().add("");
        }
    }

    public void deleteColumn(String columnName) throws IllegalArgumentException{
        int colIndex = columns.indexOf(columnName);
        if (colIndex == -1) {
            throw new IllegalArgumentException();
        }
        columns.remove(colIndex);

        // Remove corresponding values in all rows
        for (Row row : rows) {
            row.deleteValue(colIndex);
        }
    }
}

