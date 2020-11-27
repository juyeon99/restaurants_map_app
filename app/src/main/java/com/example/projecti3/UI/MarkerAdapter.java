package com.example.projecti3.UI;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import com.example.projecti3.Model.MyItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MarkerAdapter extends ArrayAdapter<MyItem> implements Filterable {

    List<MyItem> objectList;
    List<MyItem> objectListAll;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public MarkerAdapter(@NonNull Context context, int resource, List<MyItem> objectList) {
        super(context, resource);
        this.objectList = objectList;
        objectListAll=new ArrayList<>();
        objectListAll.addAll(objectList);
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
            List<MyItem> filteredList = new ArrayList<>();
            //when the input is empty, we resee all the resaturants list
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(objectListAll);
            } else {
                //search all the resaturant list
                //we find the one's name with the same order of inputs
                for (MyItem myItem: objectListAll) {
                    if (myItem.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(myItem);
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
            objectList.addAll((Collection<? extends MyItem>) results.values);
            notifyDataSetChanged();
        }
    };

}
