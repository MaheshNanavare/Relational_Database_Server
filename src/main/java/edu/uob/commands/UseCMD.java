package edu.uob.commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import edu.uob.DBServer;
import edu.uob.supporters.DBcmd;

// <Use> ::=  "USE " [DatabaseName]

public class UseCMD extends DBcmd {
    @Override
    public String query(ArrayList<String> tokens, DBServer dbServer) {
        if (tokens.size() < 2) {
            return "[ERROR] \nGive database name to be used.";
        }
    
        // Ensure the last token is a semicolon
        if (!tokens.get(tokens.size() - 1).equals(";")) {
            return "[ERROR] \nQuery must end with a semicolon (;).";
        }
        tokens.remove(tokens.size() - 1);
    
        if (tokens.size() > 2) {
            return "[ERROR] \nInvalid database name.";
        }
    
        // Get the database folder path
        String dbName = tokens.get(1).toLowerCase();
        Path databasePath = Paths.get("databases", dbName).toAbsolutePath();
    
        // Update current database
        if (Files.exists(databasePath)) {
            dbServer.setCurrentDatabase(dbName);
            return "[OK]";
        } else {
            return "[ERROR] \nDatabase does not exist.";
        }
    }        
}
