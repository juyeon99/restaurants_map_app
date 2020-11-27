package com.example.projecti3.Model;

import android.content.Context;
import android.widget.Adapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.projecti3.UI.RecentRestaurant;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import com.example.projecti3.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *    Helps to edit the clustered markers' icons (by hazard level)
 */

public class MarkerClusterRenderer extends DefaultClusterRenderer<MyItem> implements Filterable {
    ClusterManager<MyItem> objectListAll;
    ClusterManager<MyItem> objectList;
    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
         this.objectList=clusterManager;
         this.objectListAll=clusterManager;
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions options) {
        if (item.getSnippet().contains("Low")) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_map_marker));
        } else if (item.getSnippet().contains("Moderate")) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.orange_map_marker));
        } else if (item.getSnippet().contains("High")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_map_marker));
        }else{
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }
        options.snippet(item.getSnippet());
        options.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, options);
    }

    public void show(MyItem clusterItem){
        getMarker(clusterItem).showInfoWindow();
    }
    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        getMarker(clusterItem).showInfoWindow();
        //marker.showInfoWindow();
    }


    //iteration 3
    //Tutorial from https://youtu.be/CTvzoVtKoJ8
    public Filter getFilter(){
        return filter;
    }
    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //create a temp restaurant
            ClusterManager<MyItem> filteredList=null;

            //when the input is empty, we resee all the resaturants list
            if (constraint == null || constraint.length() == 0) {
                filteredList=objectList;
            } else {
                //search all the resaturant list
                //we find the one's name with the same order of inputs

                for (MyItem myItem: objectList.getAlgorithm().getItems()) {
                    if (myItem.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.addItem(myItem);
                    }
                }
            }
            //create the resurn value
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //UI behaviors
            objectList.clearItems();
            objectList.addItems((Collection<MyItem>) results.values);
       // notifyAll();
        }
    };

}