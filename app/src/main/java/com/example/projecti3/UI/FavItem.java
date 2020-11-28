package com.example.projecti3.UI;

import java.util.ArrayList;
import java.util.List;

public class FavItem {
    private String name;
    private int position;
    private String item_image;
    private int latest;

    private List<DBAdapter> favItem = new ArrayList<>();

    public List<DBAdapter> getAll(){
        return favItem;
    }

    public void add(DBAdapter db){ this.favItem.add(db);}

    public int getLatest() {
        return latest;
    }

    public void setLatest(int latest) {
        this.latest = latest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String  getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }
}
