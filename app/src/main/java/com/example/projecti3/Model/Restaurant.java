package com.example.projecti3.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *    restaurant class --> get data from restaurants
 *    can also get latest inspection report date and index
  */


public class Restaurant {
    //stores data and makes it accessible

    /*
        4th story:
        The Model:
        -Read from excel sheets and populate the data models.
        Youtube course
        https://www.youtube.com/watch?v=i-TqNzUryn8

        sqlite relationships: How to connect two different data sets
        https://developer.android.com/training/data-storage/sqlite
        https://www.youtube.com/watch?v=N-gHIJShz1I
    */
    private final List<Inspection> inspectionManager = new ArrayList<>();

    List<Integer> inspectionManagerIndex = new ArrayList<>();

    private String name;
    private String trackingNum;
    private String address;

    private Float latitude;
    private Float longitude;

    private String fav;

    private int images;
    private int hazardIcons;

    public String getTrackingNum() {
        return trackingNum;
    }

    public void setTrackingNum(String trackingNum) {
        this.trackingNum = trackingNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public void addInspection(Inspection inspection, String TrackingNum) {
        if (inspection.getTrackingNum().equals(TrackingNum)) {
            inspectionManagerIndex.add(inspection.getTrackingNum().indexOf(TrackingNum));
            inspectionManager.add(inspection);
        }
    }

    public void removeInspection(Inspection inspection) {
        this.inspectionManager.remove(inspection);
    }

    public Inspection getInspection(int i) {
        return inspectionManager.get(i);
    }

    public List<Inspection> getAllInspection(){
        return inspectionManager;
    }


    private int latest = 0;
    private int indexOfLatest = 0;
    private String latestHazard = null;
    private int latestNumIssues;

    public int getLatestInspectionDate(List<Inspection> inspection, String trackingNum) {
        for(int k = 0; k < inspection.size(); k++) {
            if (inspection.get(k).getTrackingNum().equals(trackingNum)) {
                if (inspection.get(k).getDate() > latest) {
                    this.latest = inspection.get(k).getDate();
                    this.indexOfLatest = k;
                    this.latestHazard = inspection.get(k).getHazardLevel();
                    this.latestNumIssues = inspection.get(k).getNumIssues();
                }
            }
        }
        return latest;
    }


    public int getIndexOfLatest(){
        return indexOfLatest;
    }

    public String getLatestHazard() {
        return latestHazard;
    }
    public int getLatestNumIssues() {
        return latestNumIssues;
    }
    public List<Inspection> getAllInspectionList() {
        return SingletonInspectionManager.getInstance().getAll();
    }

    public List<Restaurant> getAllRestaurants() {
        return SingletonRestaurantManager.getInstance().getAll();

    }

    @Override
    public String toString() {
        return trackingNum + ", " +
                name + ", " +
                address + ", " +
                latitude + ", " +
                longitude;
    }

    public void setFavStatus(String s) {
        this.fav = s;
    }

    public String getFavStatus() {
        return fav;
    }
}