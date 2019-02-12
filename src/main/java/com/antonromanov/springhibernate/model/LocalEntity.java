package com.antonromanov.springhibernate.model;

public class LocalEntity {


    private static LocalEntity instance;

    private LocalEntity(){
    }

    public static LocalEntity getInstance(){
        if (instance == null){
            instance = new LocalEntity();
        }
        return instance;
    }

    private int id; // id
    private String testString; //String for operate

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }
}
