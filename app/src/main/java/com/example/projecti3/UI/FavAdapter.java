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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.projecti3.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FavAdapter extends ArrayAdapter<FavItem> implements Filterable {
    private Context mContext;
    private int mResource;
    List<FavItem> objectList;
    List<FavItem> objectListAll;

    public FavAdapter(@NonNull Context context, int resource, @NonNull List<FavItem> object) {
        super(context, resource, object);
        this.mContext = context;
        this.mResource = resource;
        this.objectList=object;
        objectListAll=new ArrayList<FavItem>();
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

        TextView restName = convertView.findViewById(R.id.rest_name);
        TextView numIssues =convertView.findViewById(R.id.issues);
        TextView hazardLevel = convertView.findViewById(R.id.hzd_level);
        TextView date = convertView.findViewById(R.id.insp);
        ImageView image = convertView.findViewById(R.id.pic);
        ImageView hazardIcon = convertView.findViewById(R.id.hzd_icon);
        ImageView fav=convertView.findViewById(R.id.favListView);

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
        if(getItem(position).fav.equals("1")) {
            fav.setBackgroundResource(R.drawable.fav);
        }
        return convertView;
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
            List<FavItem> filteredList = new ArrayList<>();
            //when the input is empty, we resee all the resaturants list
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(objectListAll);
            } else {
                //search all the resaturant list
                //we find the one's name with the same order of inputs
                for (FavItem restaurant: objectListAll) {
                    if (restaurant.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(restaurant);
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
            objectList.clear();
            objectList.addAll((Collection<? extends FavItem>) results.values);
            notifyDataSetChanged();
        }
    };
}
