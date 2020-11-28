package com.example.projecti3.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import com.example.projecti3.Model.Inspection;
import com.example.projecti3.Model.Restaurant;
import com.example.projecti3.Model.SingletonRestaurantManager;
import com.example.projecti3.R;

//author: tianyu che
//this class is shows the details of a particular restaurant which clicked
// and a inspection list belongs to the particular restaurant
//  name,, address,, gps,, a listView of inspection list
public class RestaurantDetailsUI extends AppCompatActivity {
    private static final String EXTRA_RESTAURANT_INDEX = "Extra - MRD index";
    //creating attibutes
    private Restaurant restaurant;
    private TextView nameText;
    private TextView addressText;
    private TextView gpsText;
    private ListView inspectionList;
    //get the info from the privious activity
    public static Intent makeDetailIntent(Context c, int restaurantIdx){
        Intent intent=new Intent(c, RestaurantDetailsUI.class);
        intent.putExtra(EXTRA_RESTAURANT_INDEX,restaurantIdx);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details_report);

        //find where the texts are
        storeUiReferences();

        //assign the clicked restaurant in this activity as the object this activity is working on
        extractExtras(this.getIntent());

        //updates the textview
        repopulateUi();

        //get all the inspection of restaurant we are working on
        List<Inspection> orderedList = restaurant.getAllInspection();

        //helps make the inspection listn in the order from latest to the oldest
        helperOrdering(orderedList);

        //make the adapeter and fill it out in the list view
        InspectionAdapeter inspectionAdapeteradapter = new InspectionAdapeter(this, R.layout.inspection_listview, (ArrayList<Inspection>) orderedList);
        inspectionList.setAdapter(inspectionAdapeteradapter);

        inspectionList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(RestaurantDetailsUI.this, Display_Inspection.class);
                        intent.putExtra("Index:", restaurant.getAllInspection().get(position).getInspectionIndex());
                        // Toast.makeText(getApplicationContext(),"Index:" +restaurant.getAllInspection().get(position).getInspectionIndex() ,Toast.LENGTH_LONG).show();
                        RestaurantDetailsUI.this.startActivityForResult(intent, 1);
                    }
                }
        );
        //iteration 2: Back-button behaviour
        //author:tianyu che

        //iteration 2: Back-button behaviour
        //author:tianyu che
        setOnGpsCoord();
    }
    //iteration 2: Back-button behaviour
    private void setOnGpsCoord() {
        gpsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index= (int) getIntent().getIntExtra(EXTRA_RESTAURANT_INDEX,-1);

                Intent intent = new Intent(RestaurantDetailsUI.this,MapsActivity.class);
                intent.putExtra("fromRestaurantDetail",index);
                RestaurantDetailsUI.this.startActivityForResult(intent, 1);
            }
        });

    }
    //iteration 2: Back-button behaviour
    public void onBackPressed() {
        finish();
    }
    private void helperOrdering(List<Inspection> orderedList) {
        Inspection temp;
        for (int i = 0; i < orderedList.size(); i++) {
            for (int j = i + 1; j < orderedList.size(); j++) {
                if (orderedList.get(i).getDate()<(orderedList.get(j).getDate())) {

                    temp = orderedList.get(i);
                    orderedList.set(i, orderedList.get(j));
                    orderedList.set(j, temp);
                }
            }
        }
    }
    private void storeUiReferences() {
        nameText=(TextView) findViewById(R.id.nameText);
        addressText=(TextView) findViewById(R.id.addressText);
        gpsText=(TextView) findViewById(R.id.gpsText);
        inspectionList=findViewById(R.id.listView);
    }
    private void extractExtras(Intent intent) {
        int index = intent.getIntExtra(EXTRA_RESTAURANT_INDEX,-1);
        SingletonRestaurantManager restaurantManager=SingletonRestaurantManager.getInstance();
        List<Restaurant> sortedRestaurantList = restaurantManager.sortAlphabetically();
        restaurant= sortedRestaurantList.get(index);
    }
    private void repopulateUi() {
        nameText.setText(restaurant.getName());
        addressText.setText(restaurant.getAddress());
        String longitude = getApplicationContext().getResources().getString(R.string.longitude);
        String latitude = getApplicationContext().getResources().getString(R.string.latitude);
        gpsText.setText(longitude + ": "+restaurant.getLongitude()+ " " + latitude
                + ": "+restaurant.getLatitude());
    }
}
