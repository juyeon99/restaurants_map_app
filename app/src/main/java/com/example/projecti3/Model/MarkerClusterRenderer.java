package com.example.projecti3.Model;

import android.content.Context;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.projecti3.UI.RecentRestaurant;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import com.example.projecti3.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

// https://youtu.be/CTvzoVtKoJ8 searching filter
// https://www.youtube.com/watch?v=iWYsBDCGhGw SearchView

/**
 *    Helps to edit the clustered markers' icons (by hazard level)
 *    Helps to search on the map to show only the markers that contains what user searched
 */

public class MarkerClusterRenderer extends DefaultClusterRenderer<MyItem>{
    //ClusterManager<MyItem> objectListAll;
    List<MyItem> objectListAll;
    ClusterManager<MyItem> objectList;
    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
         this.objectList = clusterManager;
         objectListAll = new ArrayList<>();
         objectListAll.addAll(objectList.getAlgorithm().getItems());
         //this.objectListAll=clusterManager;
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions options) {
        //String low = String.valueOf(R.string.lowHazard);
        //String moderate = String.valueOf(R.string.moderateHazard);
        //String high = String.valueOf(R.string.highHazard);
        if (item.getSnippet().contains("Low")) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_map_marker));
        } else if (item.getSnippet().contains("Moderate")) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.orange_map_marker));
        } else if (item.getSnippet().contains("High")) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_map_marker));
        }else {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }
        options.snippet(item.getSnippet());
        options.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, options);
    }

    public void show(MyItem clusterItem) {
        getMarker(clusterItem).showInfoWindow();
    }
    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        getMarker(clusterItem).showInfoWindow();
        //marker.showInfoWindow();
    }

    public Filter getFilter(){
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //create a temp restaurant
            List<MyItem> filteredList = new ArrayList<>();
            //when the input is empty, we resee all the restaurants list
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(objectListAll);

            } else {
                //search all the restaurant list
                //we find the one's name with the same order of inputs

                // for (MyItem myItem: objectList.getAlgorithm().getItems()) {
                for (MyItem myItem: objectListAll) {
                    if (myItem.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(myItem);
                    }
                }
            }
            //create the return value
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //UI behaviors
            objectList.clearItems();
            //testing
            //MyItem myItem = new MyItem(49.19206, -122.756256, "what ever", ""+results.values,0);
            objectList.addItems((Collection<MyItem>) results.values);
            objectList.cluster();
        }
    };
}