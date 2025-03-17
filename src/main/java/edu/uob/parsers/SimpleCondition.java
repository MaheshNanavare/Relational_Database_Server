package edu.uob.parsers;

import java.util.List;

import edu.uob.dataclasses.Row;

// <SimpleCondition> ::=  [AttributeName] <Comparator> [Value] |    -No space required around comparator
//                        "(" <Condition> ")"
// <Comparator>      ::=  "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "  -Single"=" is not a comparator

public class SimpleCondition extends Condition {
    String attribute;
    String comparator;
    String value;
    
    public SimpleCondition(String attribute, String comparator, String value) {
        this.attribute = attribute;
        this.comparator = comparator;
        // RegEx-Use all characters except starting and ending single quoted
        this.value = value.replaceAll("^'(.*)'$", "$1");
    }
    
    @Override
    public boolean evaluate(Row row, List<String> cols) {
        int colIndex = -1;
        for (int i = 0; i < cols.size(); i++) {
            if (cols.get(i).equalsIgnoreCase(attribute)) {
                colIndex = i;
                break;
            }
        }
        if (colIndex == -1) return false;
        String cellValue = row.getValues().get(colIndex).replaceAll("^'(.*)'$", "$1");
        switch (comparator) {
            case "==" -> {
                return cellValue.equals(value);
            }
            case "!=" -> {
                return !cellValue.equals(value);
            }
            // Convert to Double for numerical comparators
            case ">" -> {
                try {
                    double cellNumber = Double.parseDouble(cellValue);
                    double thresholdNumber = Double.parseDouble(value);
                    return cellNumber > thresholdNumber;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "<" -> {
                try {
                    double cellNumber = Double.parseDouble(cellValue);
                    double thresholdNumber = Double.parseDouble(value);
                    return cellNumber < thresholdNumber;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case ">=" -> {
                try {
                    double cellNumber = Double.parseDouble(cellValue);
                    double thresholdNumber = Double.parseDouble(value);
                    return cellNumber >= thresholdNumber;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "<=" -> {
                try {
                    double cellNumber = Double.parseDouble(cellValue);
                    double thresholdNumber = Double.parseDouble(value);
                    return cellNumber <= thresholdNumber;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "LIKE" -> {
                return cellValue.contains(value);
            }
            default -> {
                return false;
            }
        }
    }
}
