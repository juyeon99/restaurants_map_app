package com.example.projecti3.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 *    Used for adding the items in the ClusterManager
 */

public class MyItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private int tag;

    public MyItem(double lat, double lng, String title, String snippet, int tag) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        this.tag=tag;
    }


    public LatLng getPosition() {
        return position;
    }


    public String getTitle() {
        return title;
    }


    public String getSnippet() {
        return snippet;
    }

    public int getTag() {
        return tag;
    }
}
