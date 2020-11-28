package com.example.projecti3.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

import com.example.projecti3.R;

// 1. Display list of all restaurants
// Adapter of the ListView for displaying all the lists on the first page
public class RestaurantAdapter extends ArrayAdapter<RecentRestaurant> {
    private Context mContext;
    private int mResource;

    public RestaurantAdapter(@NonNull Context context, int resource, @NonNull List<RecentRestaurant> object) {
        super(context, resource, object);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        String numInspecIssues = mContext.getResources().getString(R.string.numIssues);
        String whatHazard = mContext.getResources().getString(R.string.whatHazard);
        String theDate = mContext.getResources().getString(R.string.date);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView restName = convertView.findViewById(R.id.restaurant_name);
        TextView numIssues =convertView.findViewById(R.id.issues_found);
        TextView hazardLevel = convertView.findViewById(R.id.hazard_level);
        TextView date = convertView.findViewById(R.id.date);
        ImageView image = convertView.findViewById(R.id.icon);
        ImageView hazardIcon = convertView.findViewById(R.id.hazard_icon);

        restName.setText(getItem(position).getName());
        numIssues.setText(numInspecIssues + ": " + getItem(position).getNumIssues());
        hazardLevel.setText(whatHazard + ": " + getItem(position).getHazardLevel());
        date.setText(theDate +": " + getItem(position).getHowLongAgo());
        image.setImageResource(getItem(position).getImage());
        hazardIcon.setImageResource(getItem(position).getHazardIcon());


        if (getItem(position).getHazardLevel().equals("Low")) {
            hazardLevel.setTextColor(ContextCompat.getColor(mContext, R.color.lowLime));
        } else if (getItem(position).getHazardLevel().equals("Moderate")) {
            hazardLevel.setTextColor(ContextCompat.getColor(mContext, R.color.colorModerateOrange));
        } else if (getItem(position).getHazardLevel().equals("High")) {
            hazardLevel.setTextColor(ContextCompat.getColor(mContext, R.color.colorDangerRed));
        }
        return convertView;
    }
}