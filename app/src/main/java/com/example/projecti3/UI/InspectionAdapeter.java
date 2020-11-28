package com.example.projecti3.UI;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;

import com.example.projecti3.Model.Inspection;
import com.example.projecti3.R;

//author: tianyu che
//this is for the adapter that for the inspection list view
public class InspectionAdapeter extends ArrayAdapter<Inspection> {
    private Context context;
    private int resource;
    /**
     * Constructor. This constructor will result in the underlying data collection being
     * immutable, so methods such as {@link #clear()} will throw an exception.
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public InspectionAdapeter(@NonNull Context context, int resource, @NonNull ArrayList<Inspection> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource= resource;
    }
    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        convertView=layoutInflater.inflate(resource,parent,false);

        //cast ID
        ImageView imageView=convertView.findViewById(R.id.icon4inspectionList);
        TextView textCritial=convertView.findViewById(R.id.critical);
        TextView textNoncritical=convertView.findViewById(R.id.nonCritical);
        TextView textHowlongAgo=convertView.findViewById(R.id.howLongAgo);
        TextView textHazardlevel=convertView.findViewById(R.id.hazardLevel4inspectionList);
        //set data view
        String numCritFound = context.getResources().getString(R.string.numCritFound);
        String numNonCritFound = context.getResources().getString(R.string.numNonCritFound);
        textCritial.setText(numCritFound+": " +getItem(position).getCritIssues());
        textNoncritical.setText(numNonCritFound+ ": "+getItem(position).getNoncritIssuses());
        //makethe date to a String
        int year = getItem(position).getDate() / 10000;
        int month = (getItem(position).getDate() % 10000) / 100;
        String monthS="";
        if(month==1){
            monthS="January";
        } else if (month==2){
            monthS="February";
        } else if(month==3){
            monthS="March";
        } else if(month==4){
            monthS="April";
        } else if(month==5){
            monthS="May";
        } else if(month==6){
            monthS="June";
        } else if(month==7){
            monthS="July";
        } else if(month==8){
            monthS="August";
        } else if(month==9){
            monthS="September";
        }else if(month==10){
            monthS="October";
        }else if(month==11){
            monthS="November";
        }else if(month==12){
            monthS="December";
        }
        int day =(getItem(position).getDate() % 10000) %100;
        String dateInspec = "" + monthS + ","  + day + "," + year;

        //updates the date text
        String theDate = context.getResources().getString(R.string.date);
        textHowlongAgo.setText(theDate+ ": "+dateInspec);

        //set up the hazard level
        String whatHazard = context.getResources().getString(R.string.whatHazard) ;
        if(getItem(position).getHazardLevel().equals("")){
            textHazardlevel.setText(whatHazard + ": None");
        }else{
            textHazardlevel.setText(whatHazard + ": "+getItem(position).getHazardLevel());
        }


        //assign the color of the hazard level and change the image to the right level
        if(getItem(position).getHazardLevel().equals("Low") ) {
            textHazardlevel.setTextColor(ContextCompat.getColor(context, R.color.seagreen));
            imageView.setImageResource(R.drawable.greencircle);
        } else if (getItem(position).getHazardLevel().equals("Moderate") ) {
            textHazardlevel.setTextColor(ContextCompat.getColor(context, R.color.orange));
            imageView.setImageResource(R.drawable.orangecircle);
        } else if(getItem(position).getHazardLevel().equals("High")){
            textHazardlevel.setTextColor(ContextCompat.getColor(context, R.color.red));
            imageView.setImageResource(R.drawable.redcircle);
        }else {
            textHazardlevel.setTextColor(ContextCompat.getColor(context, R.color.colorHazardless));
            //imageView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorWhite));
        }
        return convertView;
    }
}
