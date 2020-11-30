package com.example.projecti3.Model;


public class comeFromForRD {
    private static comeFromForRD instance;
    public static comeFromForRD getInstance() {
        if (instance == null) {
            instance = new comeFromForRD();
        }
        return instance;
    }
    private String from = "";

    public String getSearchValue() {
        return from;
    }

    public void setSearchValue(String from) {
        this.from = from;
    }

}
