package edu.uob.commands;

import java.util.ArrayList;
import java.util.List;

import edu.uob.DBServer;
import edu.uob.dataclasses.Database;
import edu.uob.dataclasses.Row;
import edu.uob.dataclasses.Table;
import edu.uob.parsers.Condition;
import edu.uob.parsers.ConditionParser;
import edu.uob.supporters.DBcmd;

/*
<Select> ::= "SELECT " <WildAttribList> " FROM " [TableName] |
             "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition> 
<WildAttribList> ::= <AttributeList> | "*"
<AttributeList>  ::= [AttributeName] | [AttributeName] "," <AttributeList>
*/

public class SelectCMD extends DBcmd {

    @Override
    public String query(ArrayList<String> tokens, DBServer dbServer) {        
        // Ensure query ends with a semicolon
        if (!tokens.get(tokens.size() - 1).equals(";")) {
            return "[ERROR] \nQuery should end with a semicolon (;).";
        }
        tokens.remove(tokens.size() - 1);
        
        // Retrieve the current database
        Database database = dbServer.getDatabase(dbServer.getCurrentDatabase());
        if (database == null) {
            return "[ERROR] \nDatabase not selected.";
        }
        
        // Find the index of "FROM" token
        int fromIndex = getFromIndex(tokens);
        if (fromIndex == -1 || fromIndex == 1) {
            return "[ERROR] \nInvalid SELECT command syntax.";
        }
        
        String tableName = tokens.get(fromIndex + 1);
        Table table = database.getTable(tableName);
        if (table == null) {
            return "[ERROR] \nTable does not exist.";
        }

        // Determine requested columns
        List<String> requestedColumns = new ArrayList<>();
        boolean validCol;
        if (tokens.get(1).equals("*")) {
            if(fromIndex != 2){
                return "[ERROR] \nFROM keyword should follow *.";
            }
            requestedColumns = table.getColumns();
        } else {
            boolean needComma = false;
            for (int i = 1; i < fromIndex; i++) {
                String token = tokens.get(i);
                if(token.equals(",")) {
                    if(!needComma || i==fromIndex-1) {
                        return "[ERROR] \nComma should be used only between column names.";
                    }
                    needComma = false;
                }
                else {
                    if(needComma) {return "[ERROR] \nColumn names should be separated by comma.";}
                    needComma = true;
                    validCol = columnExists(table, token);
                    if (!validCol) {
                        return "[ERROR] \nColumn does not exist.";
                    }
                    requestedColumns.add(token);
                }
            }
        }

        // Check for optional WHERE keyword
        Condition condition = null;
        if (fromIndex + 2 < tokens.size()) {
            if (!tokens.get(fromIndex + 2).equalsIgnoreCase("WHERE")) {
                return "[ERROR] \nWHERE keyword should follow after table name.";
            }
            // Parse conditions from tokens starting at fromIndex+3 until end
            ConditionParser conditionParser = new ConditionParser();
            List<String> conditionTokens = tokens.subList(fromIndex + 3, tokens.size());
            // Give tokens with index after WHERE, get condition object
            condition = conditionParser.parse(conditionTokens); 
            if (condition == null) {
                return "[ERROR] \nInvalid WHERE clause syntax.";
            }
        }
        
        // Filter rows based on condition if applicable
        List<Row> resultRows = new ArrayList<>();
        List<String> cols = table.getColumns();
        for (Row row : table.getRows()) {
            try {
                if (condition == null || condition.evaluate(row, cols)) {
                    resultRows.add(row);
                }   
            } catch (IllegalArgumentException e) {
                return "[ERROR] \nInvalid WHERE condition syntax.";
            }
        }
        
        String displayText = getDisplayText(table, requestedColumns, resultRows);

        return displayText;
    }

    private int getFromIndex(ArrayList<String> tokens) {
        int fromIndex = -1;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("FROM")) {
                fromIndex = i;
                break;
            }
        }
        return fromIndex;
    }

    private boolean columnExists(Table table, String token) {
        boolean validCol;
        validCol = false;
        for (String tableCol : table.getColumns()) {
            if (tableCol.equalsIgnoreCase(token)) {
                validCol = true;
                break;
            }
        }
        return validCol;
    }

    private String getDisplayText(Table table, List<String> requestedColumns, List<Row> resultRows) {
        // Build result string
        StringBuilder result = new StringBuilder();
        result.append("[OK]").append("\n");
        
        // Build header based on requested columns
        result.append(String.join("\t", requestedColumns)).append("\n");
        
        // For each row, output only the requested columns
        for (Row row : resultRows) {
            if (requestedColumns == null) {
                result.append(row.toString()).append("\n");
            } else {
                List<String> rowValues = row.getValues();
                List<String> outputValues = new ArrayList<>();
                // For each requested column, get corresponding value
                for (String reqCol : requestedColumns) {
                    getColumnValues(table, reqCol, rowValues, outputValues);
                }
                result.append(String.join("\t", outputValues)).append("\n");
            }
        }
        String displayText = result.toString().trim();
        return displayText;
    }

    private void getColumnValues(Table table, String reqCol, List<String> rowValues, List<String> outputValues) {
        int idx = 0;
        List<String> tableCols = table.getColumns();
        for (int i = 0; i < tableCols.size(); i++) {
            if (tableCols.get(i).equalsIgnoreCase(reqCol)) {
                idx = i;
                break;
            }
        }
        outputValues.add(rowValues.get(idx));
    }
}