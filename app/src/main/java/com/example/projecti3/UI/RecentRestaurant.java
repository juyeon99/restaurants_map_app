package com.example.projecti3.UI;

/**
 * Can get the data needed for RestaurantAdapter
 */

public class RecentRestaurant {
    String name;
    int image;
    int numIssues;
    int hazardIcon;
    String hazardLevel;
    String howLongAgo;
    String fav;
    String numIssuesString;

    int index;
    public RecentRestaurant(String name, int image, int numIssues, int hazardIcon, String hazardLevel, String howLongAgo,String fav,int i) {
        this.name = name;
        this.image = image;
        this.numIssues = numIssues;
        this.hazardIcon = hazardIcon;
        this.hazardLevel = hazardLevel;
        this.howLongAgo = howLongAgo;
        this.fav=fav;
        this.index=i;
    }

    public void setIndex(int index){this.index=index;}
    public int getIndex(){
        return index;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getNumIssues() {
        return numIssues;
    }

    public void setNumIssues(int numIssues) {
        this.numIssues = numIssues;
    }

    public int getHazardIcon() {
        return hazardIcon;
    }

    public void setHazardIcon(int hazardIcon) {
        this.hazardIcon = hazardIcon;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }


    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public String getHowLongAgo() {
        return howLongAgo;
    }

    public void setHowLongAgo(String howLongAgo) {
        this.howLongAgo = howLongAgo;
    }
    public void setNumIssuesString(){
        this.numIssuesString=String.valueOf(numIssues);
    }
    public String getNumIssuesString(){
        return numIssuesString;
    }

}
