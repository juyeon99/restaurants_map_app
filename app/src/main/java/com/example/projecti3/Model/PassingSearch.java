package com.example.projecti3.Model;

public class PassingSearch {
    //make singleton
    private static PassingSearch instance;
    public static PassingSearch getInstance() {
        if (instance == null) {
            instance = new PassingSearch();
        }
        return instance;
    }
    private String searchValue = "";

    public String getSearchValue() {
        return searchValue;
    }
    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
    public boolean isEmpty(){
        if(this.searchValue.equals("")) {
            return true;
        } else {
            return false;
        }
    }
}
