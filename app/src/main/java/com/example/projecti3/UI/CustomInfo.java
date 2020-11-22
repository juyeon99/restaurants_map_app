package com.example.projecti3.UI;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.example.projecti3.R;

/**
 *    CustomInfo class sets the layout of the popup display when clicked the peg
 */

public class CustomInfo implements GoogleMap.InfoWindowAdapter {

    private final View window;
    private Context mContext;

    public CustomInfo(Context context) {
        mContext = context;
        window = LayoutInflater.from(context).inflate(R.layout.activity_custom_info, null);
    }

    private void rendowWindowText(Marker marker, View view){
        String title = marker.getTitle();
        TextView restaurantName = (TextView) view.findViewById(R.id.Restaurant_Name);

        if(!title.equals("")){
            restaurantName.setText(title);
            String strColor = "#0000FF";
            restaurantName.setTextColor(Color.parseColor(strColor));
        }

        String snippet = marker.getSnippet();
        TextView restaurantDetails = (TextView) view.findViewById(R.id.snippet);

        if(!snippet.equals("")){
            restaurantDetails.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker, window);
        return window;
    }
}