package edu.uob.commands;

import java.util.ArrayList;

import edu.uob.DBServer;
import edu.uob.dataclasses.Database;
import edu.uob.dataclasses.Table;
import edu.uob.supporters.DBcmd;
import edu.uob.supporters.DataLoader;

// <Alter> ::=  "ALTER " "TABLE " [TableName] " " <AlterationType> " " [AttributeName]
//                 0          1           2                3                   4
// <AlterationType> ::=  "ADD" | "DROP"

public class AlterCMD extends DBcmd {

    @Override
    public String query(ArrayList<String> tokens, DBServer dbServer) {
        // Semicolon check
        if (!tokens.get(tokens.size() - 1).equals(";")) {
            return "[ERROR] \nQuery must end with a semicolon (;).";
        }
        tokens.remove(tokens.size() - 1);
        // Basic syntax check
        if (tokens.size() < 5) {
            return "[ERROR] \nFewer arguments than expected in ALTER command syntax.";
        }
        if (tokens.size() > 5) {
            return "[ERROR] \nMore arguments than expected in ALTER command syntax.";
        }
        if (!tokens.get(1).equalsIgnoreCase("TABLE")){
            return "[ERROR] \nTABLE keyword should follow after ALTER.";
        }
        
        // Table name converted into lowercase before saving out to the filesystem
        String tableName = tokens.get(2).toLowerCase();
        String alterationType = tokens.get(3);
        String attributeName = tokens.get(4);

        if(attributeName.equalsIgnoreCase("id")){
            return "[ERROR] \nCan't alter 'id' column.";
        }

        // Check if a database is selected
        if (dbServer.getCurrentDatabase() == null) {
            return "[ERROR] \nNo database selected.";
        }
        
        // Retrieve the current database and table
        Database database = dbServer.getDatabase(dbServer.getCurrentDatabase());
        if (database == null) {
            return "[ERROR] \nDatabase not found.";
        }
        Table table = database.getTable(tableName);
        if (table == null) {
            return "[ERROR] \nTable does not exist.";
        }
        
        // Delegate task of ADD/DROP
        String output;
        output = switch (alterationType.toLowerCase()) {
            case "add" -> alterAdd(table, attributeName);
            case "drop" -> alterDrop(table, attributeName);
            default -> "[ERROR] \nInvalid ALTER syntax. Use ADD or DROP.";
        };
        
        // After altering the in-memory table, update the .tab file to reflect changes.
        String updateFileResult = DataLoader.updateTableFile(dbServer, table);
        if (!updateFileResult.equals("[OK]")) {
            output = updateFileResult;
        }
        
        return output;
    }
    
    // ADD <AttributeName>
    private String alterAdd(Table table, String attributeName) {
        if (isSQLKeyword(attributeName)) {
            return "[ERROR] \nReserved SQL keywords can't be used as column names.";
        }
        if (!isValidName(attributeName)) {
            return "[ERROR] \nColumn names must contain only letters and digits.";
        }
        try{
            table.addColumn(attributeName);
            return "[OK]";
        } catch (IllegalArgumentException e) {
            return "[ERROR] \nCan't add a column with existing name.";
        }
        
    }
    
    // DROP <AttributeName>
    private String alterDrop(Table table, String attributeName) {
        try {
            table.deleteColumn(attributeName);
            return "[OK]";
        } catch (IllegalArgumentException e) {
            return "[ERROR] \nColumn doesn't exist in table.";
        }
    }
    
}