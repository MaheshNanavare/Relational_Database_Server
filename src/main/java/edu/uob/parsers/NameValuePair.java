package edu.uob.parsers;

public class NameValuePair {

    private final String name;
    private final String value;
    private int index;

    public NameValuePair(String name, String value){
        this.name = name;
        this.value = value.replaceAll("^'(.*)'$", "$1");
        this.index = -1;
    }

    public String getName(){
        return name;
    }
    
    public String getValue(){
        return value;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
