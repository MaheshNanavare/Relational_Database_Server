package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uob.dataclasses.Database;
import edu.uob.supporters.DBcmd;
import edu.uob.supporters.DataLoader;
import edu.uob.supporters.Parser;
import edu.uob.supporters.Tokeniser;

/** This class implements the DB server. */
public class DBServer {
    //Added in template
    private List<Database> databases;  // Stores created databases
    private String currentDatabase;   // Currently selected database

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);  // Client file listening on port 8888
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        // Added class variables in template
        databases = new ArrayList<>();
        currentDatabase = null;

        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
        // Load existing databases using the new class
        DataLoader loader = new DataLoader(storageFolderPath, databases);
        loader.loadExistingDatabases();
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
        // TO DO - implement your server logic here
        String output;
        try {
            Tokeniser tokeniser = new Tokeniser(command);
            // ArrayList is implementation of List interface
            ArrayList<String> tokens = tokeniser.setup();

            Parser parser = new Parser();
            // parser command return one of the childs of DBcmd depending upon tokens in runtime.
            DBcmd commandType = parser.parse(tokens);
            // query method of object's actual class type will be invoked.
            output = commandType.query(tokens, this);  // server's instace is passed which has list of databases, currentDatabase    
        } catch (Exception e) {
            output = "[ERROR] \nQuery could not be executed.";
        }
        return output;
    }

    //// private String "currentDatabase" ////
    // 1) Method to get the current database name
    public String getCurrentDatabase() {
        return currentDatabase;     // Returns String
    }

    // 2) Method to set the current database
    public void setCurrentDatabase(String databaseName) {   // Takes String
        this.currentDatabase = databaseName;
    }

    //// private List<Database> "databases" ////
    // 1) Adds a new database
    public void addDatabase(Database db) throws IOException {
        if (db != null && !databaseExists(db.getName())) {
            databases.add(db);
        } else {
            // Caught in CatchCMD
            throw new IOException("Database already exists or invalid!");
        }
    }

    // 2) Removes a database
    public void removeDatabase(String dbName) {
        Iterator<Database> iterator = databases.iterator();
        while (iterator.hasNext()) {
            Database db = iterator.next();
            if (db.getName().equalsIgnoreCase(dbName)) {
                iterator.remove();
            }
        }
        if (currentDatabase != null && currentDatabase.equalsIgnoreCase(dbName)) {
            currentDatabase = null;  // Reset if the current database is deleted
        }
    }
    
    // 3) Checks if a database exists
    public boolean databaseExists(String dbName) {
        for (Database db : databases) {
            if (db.getName().equals(dbName)) {
                return true;  // Found a match
            }
        }
        return false;  // No match found
    }    

    // 4) Retrieves a database by name
    public Database getDatabase(String dbName) {
        for (Database db : databases) {
            if (db.getName().equalsIgnoreCase(dbName)) {
                return db;  // Found a match, return the Database object
            }
        }
        return null;  // No match found, return null
    }    

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
