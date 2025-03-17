package edu.uob.parsers;

import java.util.List;

import edu.uob.dataclasses.Row;

/*
<Condition>       ::=  [AttributeName] <Comparator> [Value] |
                       "(" <Condition> ")" |
                       <FirstCondition> <BoolOperator> <SecondCondition>
<FirstCondition>  ::=  <Condition> " " | "(" <Condition> ")"
<SecondCondition> ::=  " " <Condition> | "(" <Condition> ")"
<BoolOperator>    ::= "AND" | "OR"
<Comparator>      ::=  "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "
*/

public abstract class Condition {
    // Abstract condition class and its subclasses
    public abstract boolean evaluate(Row row, List<String> cols);
}
