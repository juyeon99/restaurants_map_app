package com.example.projecti3.Model;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import com.example.projecti3.R;

/**
 *    Helps to edit the clustered markers' icons (by hazard level)
 */

public class MarkerClusterRenderer extends DefaultClusterRenderer<MyItem> {
    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
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
}