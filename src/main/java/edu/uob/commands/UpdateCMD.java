package edu.uob.commands;

import java.util.ArrayList;
import java.util.List;

import edu.uob.DBServer;
import edu.uob.dataclasses.Database;
import edu.uob.dataclasses.Row;
import edu.uob.dataclasses.Table;
import edu.uob.parsers.Condition;
import edu.uob.parsers.ConditionParser;
import edu.uob.parsers.NameValuePair;
import edu.uob.parsers.SetParser;
import edu.uob.supporters.DBcmd;
import edu.uob.supporters.DataLoader;

// <Update> ::= "UPDATE " [TableName] " SET " <NameValueList> " WHERE " <Condition>
//  index          0          1          2       3,4,5,...      size-2    size-1

public class UpdateCMD extends DBcmd {

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
        
        // Basic error checking for UPDATE command
        String tableName = tokens.get(1);
        Table table = database.getTable(tableName);
        if (table == null) {
            return "[ERROR] \nTable does not exist.";
        }
        if(!tokens.get(2).equalsIgnoreCase("SET")) {
            return "[ERROR] \nSET keyword should be followed by table name.";
        }

        int whereIndex = -1;
        for(int i=3; i<=tokens.size()-2; i++){
            if(tokens.get(i).equalsIgnoreCase("WHERE")){
                whereIndex = i;
            }
        }
        if(whereIndex == -1) {
            return "[ERROR] \nWHERE keyword should be present before filter conditions.";
        }

        // Parse conditions from tokens starting at whereIndex until end
        ConditionParser conditionParser = new ConditionParser();
        List<String> conditionTokens = tokens.subList(whereIndex+1, tokens.size());
        Condition condition = conditionParser.parse(conditionTokens);
        if (condition == null) {
            return "[ERROR] \nInvalid WHERE clause syntax.";
        }
        
        // <NameValueList>   ::=  <NameValuePair> | <NameValuePair> "," <NameValueList>
        // <NameValuePair>   ::=  [AttributeName] "=" [Value]
        SetParser setParser = new SetParser();

        // Parse conditions from tokens starting at whereIndex until end
        List<String> setTokens = tokens.subList(3, whereIndex);
        List<NameValuePair> nameValueList;
        try {
            nameValueList = setParser.parse(setTokens);    
        } catch (IllegalArgumentException e) {
            return "[ERROR] \n" + e.getMessage();
        }

        // Verify column names and replace with column index
        List<String> cols = table.getColumns();
        Boolean validCol;
        for(NameValuePair nameValuePair: nameValueList){
            validCol = false;
            for(int i=0; i<cols.size(); i++){
                String colName = nameValuePair.getName();
                if(cols.get(i).equalsIgnoreCase(colName)){
                    validCol = true;
                    nameValuePair.setIndex(i);
                    if(i==0) { return "[ERROR] \nNot allowed allowed to edit ID column."; }
                }
            }
            if(!validCol){ return "[ERROR] \nColumn does not exist."; }
        }

        // Internal class-based database update
        String newValue;
        int idx;
        for(NameValuePair nameValuePair: nameValueList){
            newValue = nameValuePair.getValue();
            idx = nameValuePair.getIndex();
            for (Row row : table.getRows()) {
                try {
                    if (condition.evaluate(row, cols)) {
                        row.updateValue(idx, newValue);
                    }
                } catch (IllegalArgumentException e) {
                    return "[ERROR] \nInvalid WHERE condition syntax.";
                }
            }
        }

        // External file-based database update
        String output = "[OK]";
        String updateFileResult = DataLoader.updateTableFile(dbServer, table);
        if (!updateFileResult.equals("[OK]")) {
            output = updateFileResult;
        }
        return output;
    }    
}
