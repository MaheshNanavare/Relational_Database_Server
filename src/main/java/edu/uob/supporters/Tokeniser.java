package edu.uob.supporters;

import java.util.ArrayList;
import java.util.Arrays;

public class Tokeniser {
    private final String query;
    private final String[] specialCharacters = {"(",")",",",";"};

    public Tokeniser(String str){
        query = str;
    }
    
    public ArrayList<String> setup()
    {
        ArrayList<String> tokens = new ArrayList<>();
        // token_a token_b 'token_c' token_d token_e -> token_a token_b | token_c | token_d token_e
        String[] fragments = query.split("'");

        for (int i=0; i<fragments.length; i++) {
            // token_c -> 'token_c' 
            if (i%2 != 0) tokens.add("'" + fragments[i] + "'");
            // If it's not a string literal, it must be query text (which needs further processing)
            else {
                // Separates , ; ( ) and tokens
                // (a b, c); -> ( | a | b | , | c | ) | ;
                String[] nextBatchOfTokens = tokenise(fragments[i]);
                // Then copy all the tokens into the "result" list (needs a bit of conversion)
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }

        return tokens;
    }

    String[] tokenise(String input)     // (token_a, token_b, token_c);
    {
        // Add in some extra padding spaces either side of the "special characters"
        for (String specialCharacter : specialCharacters) {
            // brackets, comma, semicolon
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
            // (token_a, token_b, token_c); ->  ( token_a ,  token_b ,  token_c )  ;
        }
        
        while (input.contains("  ")) input = input.replace("  ", " ");
        input = input.trim();
        return input.split(" ");
    }
}