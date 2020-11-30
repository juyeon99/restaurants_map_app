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
    private Context mContext;

    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
         this.mContext = context;
         this.objectList = clusterManager;
         objectListAll = new ArrayList<>();
         objectListAll.addAll(objectList.getAlgorithm().getItems());
         //this.objectListAll=clusterManager;
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions options) {
        if (item.getSnippet().contains(mContext.getString(R.string.lowHazard))) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_map_marker));
        } else if (item.getSnippet().contains(mContext.getString(R.string.moderateHazard))) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.orange_map_marker));
        } else if (item.getSnippet().contains(mContext.getString(R.string.highHazard))) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_map_marker));
        } else {
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
            String[] sortedInput = constraint.toString().toLowerCase().split(",", -1);
            String name=null;
            String favrate="0";
            String hazard = null;
            String greaterOrLess=null;
            String violation=null;
            boolean isInteger=true;
            int numVio=0;
            int size=sortedInput.length;
            if(size==1){
                name=sortedInput[0].toString();
            }else if(size==2){
                name=sortedInput[0].toString();
                favrate=sortedInput[1].toString();
            }else if(size==3){
                name=sortedInput[0].toString();
                favrate=sortedInput[1].toString();
                hazard=sortedInput[2].toString();
            }else if(size==4){
                name=sortedInput[0].toString();
                favrate=sortedInput[1].toString();
                hazard=sortedInput[2].toString();
                greaterOrLess=sortedInput[3].toString();
                try {
                    numVio= Integer.parseInt(violation);
                } catch (NumberFormatException e) {
                    isInteger = false;
                }
            }else if(size==5){
                name=sortedInput[0].toString();
                favrate=sortedInput[1].toString();
                hazard=sortedInput[2].toString();
                greaterOrLess=sortedInput[3].toString();
                violation= sortedInput[4].toString();
                try {
                    numVio= Integer.parseInt(violation);
                } catch (NumberFormatException e) {
                    isInteger = false;
                }

            }else{
                name=sortedInput[0].toString();
                favrate=sortedInput[1].toString();
                hazard=sortedInput[2].toString();
                greaterOrLess=sortedInput[3].toString();
                violation= sortedInput[4].toString();
                try {
                    numVio= Integer.parseInt(violation);
                } catch (NumberFormatException e) {
                    isInteger = false;
                }
            }
            //create a temp restaurant
            List<MyItem> filteredList = new ArrayList<>();
            //when the input is empty, we resee all the restaurants list
            if (constraint == null || constraint.length() == 0) {
                //filteredList.addAll(objectListAll);
                for (MyItem myItem : objectListAll) {
                    filteredList.add(myItem);
                }
            } else {
                //search all the restaurant list
                //we find the one's name with the same order of inputs

                // for (MyItem myItem: objectList.getAlgorithm().getItems()) {
                for (MyItem myItem : objectListAll) {
//                    if (myItem.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
//                        filteredList.add(myItem);
//                    }
                    if(size==1){
                        if (myItem.getTitle().toLowerCase().contains(name)) {
                            filteredList.add(myItem);
                        }
                    }else if(size==2){
                        if (myItem.getTitle().toLowerCase().contains(name)
                                && myItem.getFavItem().contains(favrate)) {
                            filteredList.add(myItem);
                        }
                    }else if(size==3){
                        if (myItem.getTitle().toLowerCase().contains(name)
                                && myItem.getFavItem().contains(favrate)
                                && myItem.getHazardLevel().toLowerCase().contains(hazard)) {
                            filteredList.add(myItem);
                        }
                    }
                    //this is a special case
                    //the user give less or greater
                    //so we have to look index 4, which is the fifth element as well
                    else if(size>=4){
                        //we sepecify, if it is greater or less
                        //then we have to know if the user put a valid integer
                        //if not a integer, we give no result, since no element have such number of violations
                        //if have a valid integer, we specify the restaurant
                        if(greaterOrLess.contains("<=")){
                            if(isInteger){
                                if (myItem.getTitle().toLowerCase().contains(name)
                                        &&myItem.getFavItem().contains(favrate)
                                        &&myItem.getHazardLevel().toLowerCase().contains(hazard)
                                        &&myItem.getNumViolation()<=numVio) {
                                    filteredList.add(myItem);
                                }
                            }

                        }else if(greaterOrLess.contains(">=")){
                            if(isInteger){
                                if (myItem.getTitle().toLowerCase().contains(name)
                                        &&myItem.getFavItem().contains(favrate)
                                        &&myItem.getHazardLevel().toLowerCase().contains(hazard)
                                        &&myItem.getNumViolation()>=numVio) {
                                    filteredList.add(myItem);
                                }
                            }
                        }
                        //if the user did not put any thing for greater or less
                        //we generate everything match with the name, favrate and hazard level
                        else if(greaterOrLess.isEmpty()){
                            if (myItem.getTitle().toLowerCase().contains(name)
                                    &&myItem.getFavItem().contains(favrate)
                                    &&myItem.getHazardLevel().toLowerCase().contains(hazard)) {
                                filteredList.add(myItem);
                            }
                        }
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