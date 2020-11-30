package com.example.projecti3.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.projecti3.Model.SingletonRestaurantManager;
import com.example.projecti3.R;

/**
 * Adapter of the ListView which helps display all the restaurants on the restaurant list page
 */

public class RestaurantAdapter extends ArrayAdapter<RecentRestaurant> implements Filterable {
    private Context mContext;
    private int mResource;
    List<RecentRestaurant> objectList;
    List<RecentRestaurant> objectListAll;

    public RestaurantAdapter(@NonNull Context context, int resource, @NonNull List<RecentRestaurant> object) {
        super(context, resource, object);
        this.mContext = context;
        this.mResource = resource;
        this.objectList = object;
        objectListAll = new ArrayList<>();
        objectListAll.addAll(objectList);
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
        ImageView fav=convertView.findViewById(R.id.favrate);

        restName.setText(getItem(position).getName());
        numIssues.setText(numInspecIssues + ": " + getItem(position).getNumIssues());
        hazardLevel.setText(whatHazard + ": " + getItem(position).getHazardLevel());
        date.setText(theDate +": " + getItem(position).getHowLongAgo());
        image.setImageResource(getItem(position).getImage());
        hazardIcon.setImageResource(getItem(position).getHazardIcon());
        if (getItem(position).getHazardLevel().equals(mContext.getString(R.string.lowHazard))) {
            hazardLevel.setTextColor(ContextCompat.getColor(mContext, R.color.lowLime));
        } else if (getItem(position).getHazardLevel().equals(mContext.getString(R.string.moderateHazard))) {
            hazardLevel.setTextColor(ContextCompat.getColor(mContext, R.color.colorModerateOrange));
        } else if (getItem(position).getHazardLevel().equals(mContext.getString(R.string.highHazard))) {
            hazardLevel.setTextColor(ContextCompat.getColor(mContext, R.color.colorDangerRed));
        }
        if (getItem(position).fav.equals("1")) {
            fav.setBackgroundResource(R.drawable.fav);
        }
        return convertView;
    }
    //iteration 3
    //Tutorial from https://youtu.be/CTvzoVtKoJ8
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
            List<RecentRestaurant> filteredList = new ArrayList<>();
            //when the input is empty, we resee all the restaurants list
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(objectListAll);
            } else {
                //search all the restaurant list
                //we find the one's name with the same order of inputs
                for (RecentRestaurant restaurant: objectListAll) {
                    if(size==1){
                        if (restaurant.getName().toLowerCase().contains(name)) {
                            filteredList.add(restaurant);
                        }
                    }else if(size==2){
                        if (restaurant.getName().toLowerCase().contains(name)&&restaurant.fav.contains(favrate)) {
                            filteredList.add(restaurant);
                        }
                    }else if(size==3){
                        if (restaurant.getName().toLowerCase().contains(name)&&restaurant.fav.contains(favrate)&&restaurant.getHazardLevel().toLowerCase().contains(hazard)) {
                            filteredList.add(restaurant);
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
                                if (restaurant.getName().toLowerCase().contains(name)
                                        &&restaurant.fav.contains(favrate)
                                        &&restaurant.getHazardLevel().toLowerCase().contains(hazard)
                                        &&restaurant.getNumIssues()<=numVio) {
                                    filteredList.add(restaurant);
                                }
                            }

                       }else if(greaterOrLess.contains(">=")){
                            if(isInteger){
                                if (restaurant.getName().toLowerCase().contains(name)
                                        &&restaurant.fav.contains(favrate)
                                        &&restaurant.getHazardLevel().toLowerCase().contains(hazard)
                                        &&restaurant.getNumIssues()>=numVio) {
                                    filteredList.add(restaurant);
                                }
                            }
                       }
                       //if the user did not put any thing for greater or less
                        //we generate everything match with the name, favrate and hazard level
                       else if(greaterOrLess.isEmpty()){
                           if (restaurant.getName().toLowerCase().contains(name)&&restaurant.fav.contains(favrate)&&restaurant.getHazardLevel().toLowerCase().contains(hazard)) {
                               filteredList.add(restaurant);
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
            objectList.clear();
            objectList.addAll((Collection<? extends RecentRestaurant>) results.values);
            notifyDataSetChanged();
        }
    };
}
