package edu.uob.supporters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.uob.DBServer;
import edu.uob.dataclasses.Database;
import edu.uob.dataclasses.Row;
import edu.uob.dataclasses.Table;

public class DataLoader {

    private final String storageFolderPath;
    private final List<Database> databases;

    public DataLoader(String storageFolderPath, List<Database> databases) {
        this.storageFolderPath = storageFolderPath;
        this.databases = databases;
    }

    // Loads all existing databases
    public void loadExistingDatabases() {
        File databasesFolder = new File(storageFolderPath);
        if (!databasesFolder.exists()) return;

        File[] dbFolders = databasesFolder.listFiles();
        if (dbFolders == null) return;      // If no folders inside databases folder, do nothing.

        for (File dbFolder : dbFolders) {
            loadDatabase(dbFolder);     // See next method
        }
    }

    // Loads a single database
    private void loadDatabase(File dbFolder) {
        if (!dbFolder.isDirectory()) return;

        String dbName = dbFolder.getName();
        Database db = new Database(dbName);

        File[] tableFiles = dbFolder.listFiles();
        if (tableFiles != null) {
            for (File tableFile : tableFiles) {
                loadTable(db, tableFile);       // See next method
            }
        }

        databases.add(db);
    }

    // Loads a single table
    private void loadTable(Database db, File tableFile) {
        String tableName = tableFile.getName().replace(".tab", "");

        try (BufferedReader br = new BufferedReader(new FileReader(tableFile))) {
            List<String> columns = readColumns(br);
            if (columns.isEmpty()) {
                return;
            }

            Table table = new Table(tableName, columns);
            readRows(br, table);
            db.addTable(table);
        } catch (IOException e) {
        }
    }

    // Reads the first line (Column Headers)
    private List<String> readColumns(BufferedReader br) throws IOException {
        List<String> columns = new ArrayList<>();
        String headerLine = br.readLine();
        if (headerLine != null) {
            for (String col : headerLine.split("\t")) {
                columns.add(col.trim());
            }
        }
        return columns;
    }

    // Reads the remaining lines (Rows)
    private void readRows(BufferedReader br, Table table) throws IOException {
        String rowLine;
        while ((rowLine = br.readLine()) != null) {
            rowLine = rowLine.trim();
            if (rowLine.isEmpty()) continue; // Skip empty lines
            
            List<String> rowValues = new ArrayList<>();
            for (String value : rowLine.split("\t")) {
                rowValues.add(value.trim());
            }

            if (rowValues.size() != table.getColumns().size()) {
                continue;
            }

            table.addRow(rowValues);
        }
    }

    // Update external .tab file
    public static String updateTableFile(DBServer dbServer, Table table) {
        // Construct the file path using current database and table name.
        String databasePath = Paths.get("databases", dbServer.getCurrentDatabase()).toString();
        String tableFilePath = Paths.get(databasePath, table.getName() + ".tab").toString();
        File tableFile = new File(tableFilePath);
        
        // Build header line using tab separation.
        String header = String.join("\t", table.getColumns());
        // Build rows from each Row's toString() (which is tab-separated).
        StringBuilder fileContent = new StringBuilder();
        fileContent.append(header).append(System.lineSeparator());
        for (Row row : table.getRows()) {
            fileContent.append(row.toString()).append(System.lineSeparator());
        }
        
        // Write updated content to the file.
        try (FileWriter writer = new FileWriter(tableFile, false)) {
            writer.write(fileContent.toString());
        } catch (IOException e) {
            return "[ERROR] \nFailed to update table file.";
        }
        
        return "[OK]";
    }
}
