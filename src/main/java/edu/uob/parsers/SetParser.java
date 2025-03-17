package edu.uob.parsers;

import java.util.ArrayList;
import java.util.List;

// <NameValueList>   ::=  <NameValuePair> | <NameValuePair> "," <NameValueList>
// <NameValuePair>   ::=  [AttributeName] "=" [Value]
public class SetParser {

    public SetParser() {
    }

    public List<NameValuePair> parse(List<String> tokens) throws IllegalArgumentException{

        List<NameValuePair> nameValueList = new ArrayList<>();
        // Check for multiple NameValuePairs only for 3 or more tokens
        if(tokens.contains(",")) {
            getPreviousComma(tokens, nameValueList);
        } else {
            try {
                nameValueList.add(parseAssignment(tokens));
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }
        
        return nameValueList;
    }

    private void getPreviousComma(List<String> tokens, List<NameValuePair> nameValueList) {
        int previousComma = -1;
        for(int i=0; i<tokens.size(); i++){
            if(tokens.get(i).equals(",") || i==tokens.size()-1){
                if(i==tokens.size()-1){i++;}
                if(previousComma+1==i) { throw new IllegalArgumentException("No assignment given between 2 commas."); }
                List<String> assignmentTokens = tokens.subList(previousComma+1, i);
                try {
                    nameValueList.add(parseAssignment(assignmentTokens));                    
                } catch (IllegalArgumentException e) {
                    throw e;
                }

                previousComma = i;
            }
        }
    }

    // [Value]::="'"[StringLiteral]"'"|[BooleanLiteral]|[FloatLiteral]|[IntegerLiteral]|"NULL"
    public NameValuePair parseAssignment(List<String> tokens) throws IllegalArgumentException{
        // Simple assignment will have either 1, 2 or 3 tokens
        if(tokens.size()==1){
            String token = tokens.get(0);
            if(token.contains("=")) {
                int idx = token.indexOf("=");

                if(idx==0) { throw new IllegalArgumentException("Column name not given."); }
                String name = token.substring(0,idx);
                
                if(idx+1==token.length()) { throw new IllegalArgumentException("Value to set not given."); }
                String value = token.substring(idx+1,token.length());

                return new NameValuePair(name, value);
            }
        }

        if(tokens.size()==2){
            String token = tokens.get(0);
            if(token.contains("=")){
                int idx = token.indexOf("=");

                if(idx==0) { throw new IllegalArgumentException("Column name not given."); }
                String name = token.substring(0,idx);
                
                if(idx+1!=token.length()) { throw new IllegalArgumentException("Invalid value given."); }
                String value = tokens.get(1);

                return new NameValuePair(name, value);
            }

            token = tokens.get(1);
            if(token.contains("=")){
                int idx = token.indexOf("=");

                if(idx!=0) { throw new IllegalArgumentException("Invalid column name given."); }
                String name = tokens.get(0);
                
                if(idx+1==token.length()) { throw new IllegalArgumentException("Value not given."); }
                String value = token.substring(idx+1,token.length());

                return new NameValuePair(name, value);
            }
        }

        if(tokens.size()==3){
            if(tokens.get(1).equals("=")){
                String name = tokens.get(0);
                String value = tokens.get(2);

                return new NameValuePair(name, value);
            }
        }

        throw new IllegalArgumentException("No '=' sign present.");
    }
}
