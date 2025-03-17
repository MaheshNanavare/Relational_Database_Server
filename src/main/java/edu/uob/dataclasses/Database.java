package edu.uob.dataclasses;

import java.util.HashMap;
import java.util.Map;

/* 
DBServer has list of database objects
Database has hashmap of table objects
Table has 2 lists of column names (Strings) and row objects
Row has list of values (Strings)
*/

public class Database {
    private final String name;
    private final Map<String, Table> tables;

    public Database(String name) {
        this.name = name;
        this.tables = new HashMap<>();
    }

    // Correct implementation of getName()
    public String getName() {
        return name;
    }

    // Method to add a table
    public void addTable(Table table) {
        tables.put(table.getName().toLowerCase(), table);
    }

    // Method to remove a table
    public void removeTable(String tableName) {
        tables.remove(tableName);
    }

    // Check if a table exists
    public boolean tableExists(String tableName) {
        return tables.containsKey(tableName.toLowerCase());
    }

    // Retrieve a table by name
    public Table getTable(String tableName) {
        return tables.get(tableName.toLowerCase());
    }
}


