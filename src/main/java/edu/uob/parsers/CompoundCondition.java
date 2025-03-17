package edu.uob.parsers;

import java.util.List;

import edu.uob.dataclasses.Row;

/*
<Condition>       ::=  <FirstCondition> <boolOperatorerator> <SecondCondition>
<FirstCondition>  ::=  <Condition> " " | "(" <Condition> ")"
<SecondCondition> ::=  " " <Condition> | "(" <Condition> ")"
<boolOperatorerator>    ::= "AND" | "OR"
*/

public class CompoundCondition extends Condition {
    Condition leftCondition;
    String boolOperator;
    Condition rightCondition;
    
    public CompoundCondition(Condition leftCondition, String boolOperator, Condition rightCondition) {
        this.leftCondition = leftCondition;
        this.boolOperator = boolOperator;
        this.rightCondition = rightCondition;
    }
    
    @Override
    public boolean evaluate(Row row, List<String> cols) throws IllegalArgumentException{
        if(leftCondition==null || rightCondition==null){
            throw new IllegalArgumentException();
        }
        if (boolOperator.equalsIgnoreCase("AND")) {
            return leftCondition.evaluate(row, cols) && rightCondition.evaluate(row, cols);
        } else if (boolOperator.equalsIgnoreCase("OR")) {
            return leftCondition.evaluate(row, cols) || rightCondition.evaluate(row, cols);
        }
        return false;
    }
}
