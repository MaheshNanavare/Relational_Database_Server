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
import edu.uob.supporters.DataLoader;

// <Delete> ::= "DELETE " "FROM " [TableName] " WHERE " <Condition>
//  index          0        1          2          3      4,5,6,...

public class DeleteCMD extends DBcmd {

    @Override
    public String query(ArrayList<String> tokens, DBServer dbServer) {
        if (!tokens.get(tokens.size() - 1).equals(";")) {
            return "[ERROR] \nQuery should end with a semicolon (;).";
        }
        tokens.remove(tokens.size() - 1);
        
        Database database = dbServer.getDatabase(dbServer.getCurrentDatabase());
        if (database == null) {
            return "[ERROR] \nDatabase not selected.";
        }
        
        // Basic error checking for DELETE command
        if(!tokens.get(1).equalsIgnoreCase("FROM")) {
            return "[ERROR] \nDELETE keyword should be followed by FROM keyword.";
        }
        String tableName = tokens.get(2);
        Table table = database.getTable(tableName);
        if (table == null) {
            return "[ERROR] \nTable does not exist.";
        }
        if(!tokens.get(3).equalsIgnoreCase("WHERE")) {
            return "[ERROR] \nWHERE keyword should be followed by table name.";
        }
        if(tokens.size()<5){
            return "[ERROR] \nNo condition provided.";
        }

        // Parse conditions from tokens starting at index 4 until end
        ConditionParser conditionParser = new ConditionParser();
        List<String> conditionTokens = tokens.subList(4, tokens.size());

        // Give tokens with index after WHERE, get condition object
        Condition condition = conditionParser.parse(conditionTokens);
        if (condition == null) {
            return "[ERROR] \nInvalid WHERE clause syntax.";
        }
        
        // Internal class-based database update
        List<Row> resultRows = new ArrayList<>();
        List<String> cols = table.getColumns();
        for (Row row : table.getRows()) {
            try {
                if (!condition.evaluate(row, cols)) {
                    resultRows.add(row);
                }   
            } catch (IllegalArgumentException e) {
                return "[ERROR] \nInvalid WHERE condition syntax.";
            }
        }
        table.setRows(resultRows);

        // External file-based database update
        String output = "[OK]";
        String updateFileResult = DataLoader.updateTableFile(dbServer, table);
        if (!updateFileResult.equals("[OK]")) {
            output = updateFileResult;
        }
        return output;
    }
}

