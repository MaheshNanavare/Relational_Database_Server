package edu.uob.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uob.DBServer;
import edu.uob.dataclasses.Database;
import edu.uob.dataclasses.Table;
import edu.uob.supporters.DBcmd;

// <Create>  ::=  <CreateDatabase> | <CreateTable>

public class CreateCMD extends DBcmd {

    @Override    
    public String query(ArrayList<String> tokens, DBServer dbServer) {
        if (tokens.size() == 1) { 
            return "[ERROR] \nEither create database or table."; 
        }
        // Ensure the last token is a semicolon
        if (!tokens.get(tokens.size() - 1).equals(";")) {
            return "[ERROR] \nQuery must end with a semicolon (;).";
        } 
        tokens.remove(tokens.size() - 1);
    
        // Delegates to either createDatabase or createTable
        return switch (tokens.get(1).toLowerCase()) {
            case "database" -> createDatabase(tokens, dbServer);
            case "table" -> createTable(tokens, dbServer);
            default -> "[ERROR] \nOnly table and database can be created.";
        };
    }    

    // <CreateDatabase>  ::=  "CREATE " "DATABASE " [DatabaseName]
    //     index                 0          1             2
    private String createDatabase(ArrayList<String> tokens, DBServer dbServer) {
        // Basic query ERROR checking
        if (tokens.size() > 3) { return "[ERROR] \nToo many arguments for database name."; }
        if (tokens.size() == 2) { return "[ERROR] \nGive a name for the database to be created."; }

        // tokens.get(1) is DATABASE keyword.
        String dbName = tokens.get(2).toLowerCase();
        if (isSQLKeyword(dbName)) {
            return "[ERROR] \nReserved SQL keywords can't be used as database names.";
        }
        if (!isValidName(dbName)) {
            return "[ERROR] \nDatabase name must contain only letters and digits.";
        }
        String storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        String newFolderPath = Paths.get(storageFolderPath, dbName).toString();
        Path folderPath = Paths.get(newFolderPath);

        if (Files.exists(folderPath)) {
            return "[ERROR] \nDatabase with the same name already exists.";
        } else {
            try {
                Files.createDirectories(folderPath);
                dbServer.addDatabase(new Database(dbName)); 
                return "[OK]";
            } catch (IOException ioe) {
                return "[ERROR] \nCould not create database.";
            }
        }
    }

    // <CreateTable> ::= "CREATE " "TABLE " [TableName] | 
    //      index           0         1         2       |
    //                   "CREATE " "TABLE " [TableName] "(" <AttributeList> ")"
    //                      0         1         2         3  4,5,... tokens.size()-1
    private String createTable(ArrayList<String> tokens, DBServer dbServer) {
        // Basic query ERROR checking
        if (dbServer.getCurrentDatabase() == null) { 
            return "[ERROR] \nNo database selected."; 
        }
        if (tokens.size() < 3) { 
            return "[ERROR] \nGive a name for the table to be created."; 
        }
    
        // tokens.get(1) is TABLE keyword.
        String tableName = tokens.get(2).toLowerCase();
        if (isSQLKeyword(tableName)) {
            return "[ERROR] \nReserved SQL keywords can't be used as table names.";
        }
        if (!isValidName(tableName)) {
            return "[ERROR] \nTable names must contain only letters and digits.";
        }
    
        String databasePath = Paths.get("databases", dbServer.getCurrentDatabase()).toString(); 
        String tableFilePath = Paths.get(databasePath, tableName + ".tab").toString();
        File tableFile = new File(tableFilePath);
    
        if (tableFile.exists()) {
            return "[ERROR] \nTable already exists.";
        }
    
        List<String> columnNames = new ArrayList<>();
        if (tokens.size() > 3) {
            if (tokens.indexOf("(") != 3 || tokens.indexOf(")") != tokens.size()-1) { 
                return "[ERROR] \nAttribute list should be followed by Table names inside brackets ().";
            }
            try {
                columnNames = extractColumnNames(tokens);
            } catch (IllegalArgumentException e) {
                return "[ERROR] \n"+e.getMessage();
            }
        }
        // Validate extracted column names
        Set<String> uniqueColumns = new HashSet<>();
        for (String col : columnNames) {
            if (isSQLKeyword(col)) {
                return "[ERROR] \nReserved SQL keywords can't be used as column names.";
            }
            if (col.equalsIgnoreCase("id")) {
                return "[ERROR] \nColumn with 'ID' name can't be added by user.";
            }
            if (!isValidName(col)) {
                return "[ERROR] \nColumn names must contain only letters and digits.";
            }
            if (!uniqueColumns.add(col.toLowerCase())) {
                return "[ERROR] \nAll columns must have unique names.";
            }
        }
        columnNames.add(0, "id");
    
        // Create file and write column names
        try {
            Files.createFile(Paths.get(tableFilePath));
        } catch (IOException e) {
            return "[ERROR] \nFailed to create table file.";
        }
    
        try (FileWriter writer = new FileWriter(tableFile)) {
            writer.write(String.join("\t", columnNames) + System.lineSeparator());
        } catch (IOException e) {
            return "[ERROR] \nFailed to write column names to table file.";
        }
    
        // Save table internally in Database
        Database database = dbServer.getDatabase(dbServer.getCurrentDatabase());
        if (database != null) {
            Table newTable = new Table(tableName, columnNames);
            database.addTable(newTable);
        }
    
        return "[OK]";
    }

    private List<String> extractColumnNames(ArrayList<String> tokens) throws IllegalArgumentException{
        int start = tokens.indexOf("(");
        int end = tokens.indexOf(")");
    
        if (end-start == 1) {
            return new ArrayList<>();
        }
    
        List<String> columnTokens = new ArrayList<>();
        boolean needComma = false;
        for(int i=start + 1; i<end; i++){
            String token = tokens.get(i);
            if(token.equals(",")) {
                if(!needComma || i==end-1) {
                    throw new IllegalArgumentException("Comma should be used only between column names.");
                }
                needComma = false;
            }
            else {
                if(needComma) {
                    throw new IllegalArgumentException("Column names should be separated by comma.");
                }
                needComma = true;
                columnTokens.add(token);
            }
        }
        return columnTokens;
    }    
}