package edu.uob.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import edu.uob.DBServer;
import edu.uob.supporters.DBcmd;

//*** DONE refactoring ***//
// <Drop> ::= "DROP " "DATABASE " [DatabaseName] | "DROP " "TABLE " [TableName]

public class DropCMD extends DBcmd {

    @Override
    public String query(ArrayList<String> tokens, DBServer dbServer) {
        // Basic query errro checking
        if (tokens.size() == 1) { return "[ERROR] \nEither drop database or table."; }
        if (!tokens.get(tokens.size() - 1).equals(";")) {
            return "[ERROR] \nQuery must end with a semicolon (;).";
        } 
        tokens.remove(tokens.size() - 1);

        // Delegate to either dropDatabase or dropTable
        return switch (tokens.get(1).toLowerCase()) {
            case "database" -> dropDatabase(tokens, dbServer);
            case "table" -> dropTable(tokens, dbServer);
            default -> "[ERROR] \nOnly table and database can be dropped.";
        };
    }

    private String dropDatabase(ArrayList<String> tokens, DBServer dbServer) {
        if (tokens.size() > 3) { return "[ERROR] \nToo many arguments passed for database name."; }
        if (tokens.size() == 2) { return "[ERROR] \nSpecify the database name to drop."; }

        String dbName = tokens.get(2);
        String dbPath = Paths.get("databases", dbName).toAbsolutePath().toString();
        File dbFolder = new File(dbPath);

        if (!dbFolder.exists()) {
            return "[ERROR] \nDatabase does not exist: " + dbName;
        }

        try {
            // Delete the database folder and its contents (externally)
            deleteDirectory(dbFolder);

            // Remove from DBServer's list of databases (internally)
            dbServer.removeDatabase(dbName);
        } catch (IOException e) {
            return "[ERROR] \nDatabase could not be deleted.";
        }
        
        return "[OK]";
    }

    private String dropTable(ArrayList<String> tokens, DBServer dbServer) {
        String currentDBName = dbServer.getCurrentDatabase();
        if (currentDBName == null) {
            return "[ERROR] \nNo database selected.";
        }
        if (tokens.size() > 3) { return "[ERROR] \nToo many arguments passed for table name."; }
        if (tokens.size() == 2) { return "[ERROR] \nSpecify the table name to drop."; }

        String tableName = tokens.get(2);
        String tableFileName = tableName + ".tab";
        String filePath = Paths.get("databases", dbServer.getCurrentDatabase(), tableFileName).toString();
        File tableFile = new File(filePath);

        if (!tableFile.exists()) {
            return "[ERROR] \nTable does not exist.";
        }

        tableFile.delete();     // externally
        dbServer.getDatabase(currentDBName).removeTable(tableName);     // internally
        return "[OK]";
    }

    // Helper method to delete directories recursively
    private void deleteDirectory(File file) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteDirectory(child);
            }
        }
        Files.delete(file.toPath());
    }
}