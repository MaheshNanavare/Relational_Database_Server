package edu.uob.supporters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uob.DBServer;

// <Command>     ::=  <CommandType> ";"
// <CommandType> ::= <Use>|<Create>|<Drop>|<Alter>|<Insert>|<Select>|<Update>|<Delete>|<Join>

public abstract class DBcmd {
    private static final List<String> SQL_KEYWORDS = Arrays.asList(
    "SELECT", "FROM", "WHERE", "AND", "OR", "INSERT", "INTO", "VALUES", "DELETE", "UPDATE",
    "SET", "CREATE", "TABLE", "DROP", "ALTER", "ADD", "DATABASE", "USE", "LIKE", "JOIN");

    // Abstract method needs to be implemented by all child classes
    public abstract String query(ArrayList<String> tokens, DBServer dbServer);

    // Accessible by child classes
    protected boolean isSQLKeyword(String name) {
        return SQL_KEYWORDS.contains(name.toUpperCase());
    }

    // Allow only letters and digits in database, table, column names
    protected boolean isValidName(String name) {
        return name.matches("[a-zA-Z0-9]+");
    }
}