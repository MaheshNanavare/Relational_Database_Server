package edu.uob.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.uob.DBServer;
import edu.uob.dataclasses.Database;
import edu.uob.dataclasses.Row;
import edu.uob.dataclasses.Table;
import edu.uob.supporters.DBcmd;

// <Insert>    ::=  "INSERT " "INTO " [TableName] " VALUES" "(" <ValueList> ")"
//   index             0        1          2         3      4    5,6,7,..  tokens.size()-1
// <ValueList> ::=  [Value] | [Value] "," <ValueList>
// [Value]     ::=  "'"[StringLiteral]"'"|[BooleanLiteral]|[FloatLiteral]|[IntegerLiteral]|"NULL"
public class InsertCMD extends DBcmd {

    @Override
    public String query(ArrayList<String> tokens, DBServer dbServer) {
        // Basic query ERRORs
        if (!tokens.get(tokens.size() - 1).equals(";")) { return "[ERROR] \nQuery must end with a semicolon (;)."; }
        tokens.remove(tokens.size() - 1);
        if (!tokens.get(1).equalsIgnoreCase("INTO") || !tokens.get(3).equalsIgnoreCase("VALUES")) {
            return "[ERROR] \nInvalid INSERT command syntax.";
        }
        int start = tokens.indexOf("(");
        int end = tokens.indexOf(")");
        if (start!=4 || end!=tokens.size()-1) {
            return "[ERROR] \nNeed to have values in brackets ().";
        }
        if (dbServer.getCurrentDatabase() == null) {
            return "[ERROR] \nNo database selected.";
        }
        String tableName = tokens.get(2);

        // Comma location errors
        List<String> values = new ArrayList<>();
        boolean needComma = false;
        for(int i=start + 1; i<end; i++){
            String token = tokens.get(i);
            if(token.equals(",")) {
                if(!needComma || i==end-1) {
                    return "[ERROR] \nComma should be used only between values.";
                }
                needComma = false;
            }
            else {
                if(needComma) {return "[ERROR] \nValues names should be separated by comma.";}
                needComma = true;
                if(stringLiteralWithoutQuotes(token)) {
                    return "[ERROR] \nString literal should be enclosed inside pair of single quotes.";
                }
                values.add(token.replaceAll("^'(.*)'$", "$1"));
            }
        }

        return insertIntoTable(dbServer, tableName, values);
    }

    private String insertIntoTable(DBServer dbServer, String tableName, List<String> userValues) {
        // Retrieve the table from the database
        String currentDBName = dbServer.getCurrentDatabase();
        Database database = dbServer.getDatabase(currentDBName);
        Table table = database.getTable(tableName);

        if (table == null) {
            return "[ERROR] \nTable does not exist.";
        }
        
        // Ensure same number of values as columns (except id column)
        if (userValues.size() != table.getColumns().size() - 1) {
            return "[ERROR] \nNumber of values does not match number of columns.";
        }
        
        // Generate unique id for new row inserted
        int newId;
        List<Row> rows = table.getRows();

        if (rows.isEmpty()) {
            newId = 1;
        }
        else{
            // Get ID from the last row
            int lastId = Integer.parseInt(rows.get(rows.size() - 1).getValues().get(0));
            newId = lastId + 1;
        }
        
        // Prepend the generated id to the user provided values.
        List<String> newRowValues = new ArrayList<>();
        newRowValues.add(String.valueOf(newId));
        newRowValues.addAll(userValues);
        
        // Add row to table (updates internal database)
        try {
            table.addRow(newRowValues);
        } catch (IllegalArgumentException e) {
            return "[ERROR] \n" + e.getMessage();
        }

        // Append new row to table's .tab file (updates external database)
        String databasePath = Paths.get("databases", dbServer.getCurrentDatabase()).toString();
        String tableFilePath = Paths.get(databasePath, tableName + ".tab").toString();
        File tableFile = new File(tableFilePath);
        try (FileWriter writer = new FileWriter(tableFile, true)) {
            writer.write(String.join("\t", newRowValues) + System.lineSeparator());
        } catch (IOException e) {
            return "[ERROR] \nFailed to update table file: " + e.getMessage();
        }
        
        return "[OK]";
    }

    public static boolean stringLiteralWithoutQuotes(String input) {
        if (input.equalsIgnoreCase("NULL")) {
            return false;
        }
        
        // Check for boolean
        if (input.equalsIgnoreCase("TRUE") || input.equalsIgnoreCase("FALSE")) {
            return false;
        }
        
        // Check for integer
        try {
            Integer.parseInt(input);
            return false;
        } catch (NumberFormatException e) {
        }
        
        // Check for float
        try {
            Float.parseFloat(input);
            if (input.contains(".")) {
                return false;
            }
        } catch (NumberFormatException e) {
        }
        
        // Check if plain text is not enclosed in single quotes
        return !(input.startsWith("'") && input.endsWith("'"));
    }
}