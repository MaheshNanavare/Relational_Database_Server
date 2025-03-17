package edu.uob.commands;

import java.util.ArrayList;
import java.util.List;

import edu.uob.DBServer;
import edu.uob.dataclasses.Database;
import edu.uob.dataclasses.Row;
import edu.uob.dataclasses.Table;
import edu.uob.supporters.DBcmd;

// <Join> ::= "JOIN " [TableName] " AND " [TableName] " ON " [AttributeName] " AND " [AttributeName]
// index        0          1         2         3        4           5           6          7
public class JoinCMD extends DBcmd {

    @Override
    public String query(ArrayList<String> tokens, DBServer dbServer) {
        // Ensure query ends with a semicolon
        if (!tokens.get(tokens.size() - 1).equals(";")) {
            return "[ERROR] \nQuery should end with a semicolon (;).";
        }
        tokens.remove(tokens.size() - 1);
        // Basic JOIN query syntax
        if(tokens.size()>8) { return "[ERROR] \nToo many arguments given than expected for JOIN command.";}
        if(tokens.size()<8) { return "[ERROR] \nToo few arguments given than expected for JOIN command.";}
        boolean firstAnd = tokens.get(2).equalsIgnoreCase("AND");
        boolean on = tokens.get(4).equalsIgnoreCase("ON");
        boolean secondAnd = tokens.get(6).equalsIgnoreCase("AND");
        if (!firstAnd || !on || !secondAnd) {
            return "[ERROR] \nInvalid JOIN syntax.";
        }
        
        // Retrieve the current database
        Database database = dbServer.getDatabase(dbServer.getCurrentDatabase());
        if (database == null) {
            return "[ERROR] \nDatabase not selected.";
        }
        
        String first = tokens.get(1);
        String second = tokens.get(3);
        Table firstTable = database.getTable(first);
        Table secondTable = database.getTable(second);
        if (firstTable==null || secondTable==null) {
            return "[ERROR] \nTable does not exist.";
        }

        // Merged table column names
        List<String> newCols = new ArrayList<>();
        newCols.add("id");
        // Verify column names
        String firstCol = tokens.get(5);
        int index1 = -1;
        boolean validCol = false;
        List<String> firstCols = firstTable.getColumns();
        for (int i=0; i<firstCols.size(); i++) {
            String col = firstCols.get(i);
            if (firstCol.equalsIgnoreCase(col)) {
                validCol = true;
                index1 = i;
            } else if(!col.equalsIgnoreCase("id")) {
                newCols.add(first+"."+col);
            }
        }
        if (!validCol) {
            return "[ERROR] \nColumn does not exist.";
        }
        String secondCol = tokens.get(7);
        int index2 = -1;
        validCol = false;
        List<String> secondCols = secondTable.getColumns();
        for (int i=0; i<secondCols.size(); i++) {
            String col = secondCols.get(i);
            if (secondCol.equalsIgnoreCase(col)) {
                validCol = true;
                index2 = i;
            } else if(!col.equalsIgnoreCase("id")) {
                newCols.add(second+"."+col);
            }
        }
        if (!validCol) {
            return "[ERROR] \nColumn does not exist.";
        }

        // Build result string for actual joining tables
        StringBuilder result = new StringBuilder();
        result.append("[OK]").append("\n");
        
        // Build header based on requested columns
        result.append(String.join("\t", newCols)).append("\n");

        //  Include a new ID column containing freshly generated IDs
        int newId = 1;

        // For each row, output only the requested columns
        List<Row> firstRows = firstTable.getRows();
        List<Row> secondRows = secondTable.getRows();

        String value1, value2;
        for (Row row1 : firstRows) {
            value1 = row1.getValues().get(index1);
            for (Row row2 : secondRows){
                value2 = row2.getValues().get(index2);
                if(value1.equals(value2)){
                    result.append(newId);
                    newId++;
                    addRowValues(index1, row1, firstCols, result);
                    addRowValues(index2, row2, secondCols, result);
                    result.append("\n");
                }
            }
        }
        return result.toString().trim();
    }

    private void addRowValues(int index, Row row, List<String> columns, StringBuilder result) {
        for(int i=1; i<columns.size(); i++){
            if(i!=index){
                result.append("\t");
                result.append(row.getValues().get(i));
            }
        }
    }
}
