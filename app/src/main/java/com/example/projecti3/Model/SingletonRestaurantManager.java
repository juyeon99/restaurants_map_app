package com.example.projecti3.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *    singleton restaurant manager --> makes restaurant list iterable
 *    can also sort them alphabetically
 */

public class SingletonRestaurantManager implements Iterable<Restaurant> {


    private List<Restaurant> restaurantManager = new ArrayList<>();

    private static SingletonRestaurantManager instance;

    public static SingletonRestaurantManager getInstance() {
        if (instance == null) {
            instance = new SingletonRestaurantManager();
        }
        return instance;
    }

    private SingletonRestaurantManager() {
        // Nothing: ensure this is a singleton.
    }

    public void add(Restaurant restaurant) {
        this.restaurantManager.add(restaurant);
    }

    public void remove(Restaurant restaurant) {
        this.restaurantManager.remove(restaurant);
    }

    public Restaurant get(int i) {
        return restaurantManager.get(i);
    }

    public List<Restaurant> getAll(){
        return restaurantManager;
    }

    public int findIndexbyTrackNum(String trackNum){
        for(int i=0;i<this.getNumRestaurants();i++){
            if(trackNum.equals(this.getTrackingNum(i))){
                return i;
            }

        }
        return -1;
    }

    public String getTrackingNum(int i){
        return restaurantManager.get(i).getTrackingNum();
    }

    public List<Inspection> getAllInspections(int i){
        return restaurantManager.get(i).getAllInspection();
    }

    public List<Restaurant> sortAlphabetically(){
        Restaurant temp;

        for (int i = 0; i < restaurantManager.size(); i++) {
            for (int j = i + 1; j < restaurantManager.size(); j++) {
                if (restaurantManager.get(i).getName().compareTo(restaurantManager.get(j).getName()) > 0) {
                    temp = restaurantManager.get(i);
                    restaurantManager.set(i, restaurantManager.get(j));
                    restaurantManager.set(j, temp);
                }
            }
        }
        return restaurantManager;
    }
    public List<Restaurant> getRestaurantManager(){
        return restaurantManager;
    }
    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantManager.iterator();
    }

    public void setFav(int index, String fav){
        this.restaurantManager.get(index).setFavStatus(fav);
    }

    public int getNumRestaurants() {
        return restaurantManager.size();
    }
    public String getStoreDataKey() {
        //String stuff;

        return "RestarauntDetailsKey";
    }
    public String getDownloadDateKey() {

        return "RestarauntDownloadsDateKey";
    }
    public String getLastModifiedDateKey() {

        return "RestarauntLastModified";
    }
    @Override
    public String toString() {
        return restaurantManager +
                "";
    }
}