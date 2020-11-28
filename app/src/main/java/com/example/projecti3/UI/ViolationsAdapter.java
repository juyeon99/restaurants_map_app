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

import java.util.ArrayList;

import com.example.projecti3.R;

/**
 *
 * This class will accept a list of data from the ViolationPerson class to create a list out of that data
 */
public class ViolationsAdapter extends ArrayAdapter<ViolationPerson> {
    private Context mContext;
    private int mResource;
    public ViolationsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ViolationPerson> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView imageView = convertView.findViewById(R.id.critLevel);
        ImageView imageView2 = convertView.findViewById(R.id.violationNature);
        TextView violationFiller = convertView.findViewById(R.id.violation);
        String[] viol = null;
        imageView.setImageResource(getItem(position).getImage());
        imageView2.setImageResource(getItem(position).getImage2());
        String[] data = getItem(position).getViolation().split(",");

        violationFiller.setText(data[0]);
        if (getItem(position).getViolation().contains("Not Critical")) {
               violationFiller.setTextColor(ContextCompat.getColor(mContext, R.color.lowLime));
        } else {
            violationFiller.setTextColor(ContextCompat.getColor(mContext, R.color.colorModerateOrange));
        }


        return convertView;
    }
}
