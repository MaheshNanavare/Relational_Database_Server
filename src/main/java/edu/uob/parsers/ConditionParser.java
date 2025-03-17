package edu.uob.parsers;

import java.util.Arrays;
import java.util.List;

// <Comparator> ::= "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "

public class ConditionParser {

    private static final List<String> comparators = Arrays.asList("==",">","<",">=","<=","!=","LIKE");

    public ConditionParser() {
    }

    public Condition parse(List<String> tokens) {
        Condition output = null;

        // Check for compound condition only for 3 or more tokens
        if(tokens.size()>=3){
            for(int i=0; i<tokens.size(); i++){
                if(tokens.get(i).equalsIgnoreCase("AND") || tokens.get(i).equalsIgnoreCase("OR")){
                    return getCompoundCondition(tokens, i);
                }
            }    
        }
        
        // Remove bracket pair
        if(tokens.get(0).equals("(")){
            if(tokens.get(tokens.size()-1).equals(")")){
                tokens = tokens.subList(1, tokens.size()-1);
            }
            else{
                return null;
            }
        }

        // Simple condition wihtout brackets will have either 1, 2 or 3 tokens
        if(tokens.size()==1){
            String token = tokens.get(0);
            for(String comparator: comparators){
                if(token.contains(comparator)){
                    return getSimpleCondition(token, comparator);
                }
            }
        }

        if(tokens.size()==2){
            String token = tokens.get(0);
            for(String comparator: comparators){
                if(token.contains(comparator)){
                    int idx = token.indexOf(comparator);

                    // No attribute given
                    if(idx==0) {return null;}
                    String attribute = token.substring(0,idx);
                    
                    // Invalid string after comparator
                    if(idx+comparator.length()!=token.length()) {return null;}
                    String value = tokens.get(1);

                    return new SimpleCondition(attribute, comparator, value);
                }
            }

            token = tokens.get(1);
            for(String comparator: comparators){
                if(token.contains(comparator)){
                    int idx = token.indexOf(comparator);

                    // Invalid string before comparator
                    if(idx!=0) {return null;}
                    String attribute = tokens.get(0);
                    
                    // No value given
                    if(idx+comparator.length()==token.length()) {return null;}
                    String value = token.substring(idx+comparator.length(),token.length());

                    return new SimpleCondition(attribute, comparator, value);
                }
            }
        }

        if(tokens.size()==3){
            if(comparators.contains(tokens.get(1))){
                String attribute = tokens.get(0);
                String comparator = tokens.get(1);
                String value = tokens.get(2);

                return new SimpleCondition(attribute, comparator, value);
            }
        }

        return output;
    }

    private Condition getSimpleCondition(String token, String comparator) {
        int idx = token.indexOf(comparator);

        if(idx==0) {return null;}    // No attribute
        String attribute = token.substring(0,idx);
        
        if(idx+comparator.length()==token.length()) {return null;}    // No value
        String value = token.substring(idx+comparator.length(),token.length());

        return new SimpleCondition(attribute, comparator, value);
    }

    private Condition getCompoundCondition(List<String> tokens, int boolOpIndx) {
        List<String> left = tokens.subList(0, boolOpIndx);
        String boolOperator = tokens.get(boolOpIndx);
        List<String> right = tokens.subList(boolOpIndx+1, tokens.size());
        
        ConditionParser leftConditionParser = new ConditionParser();
        Condition leftCondition = leftConditionParser.parse(left);
         
        ConditionParser rightConditionParser = new ConditionParser();
        Condition rightCondition = rightConditionParser.parse(right);

        if(leftCondition==null || rightCondition==null){
            return null;
        }
        return new CompoundCondition(leftCondition, boolOperator, rightCondition);
    }
}
