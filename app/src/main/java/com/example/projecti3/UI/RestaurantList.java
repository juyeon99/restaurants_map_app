package com.example.projecti3.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projecti3.Model.Inspection;
import com.example.projecti3.Model.PassingSearch;
import com.example.projecti3.Model.Restaurant;
import com.example.projecti3.Model.SingletonRestaurantManager;
import com.example.projecti3.Model.comeFromForRD;
import com.example.projecti3.R;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Code found at:
// https://www.youtube.com/watch?v=i-TqNzUryn8 reading CSV file
// https://www.youtube.com/watch?v=aMw4d7uWNW8 Sorting Arraylist Alphabetically
// https://www.youtube.com/watch?v=dZQqrPdqT1E Sorting Arraylist Alphabetically
// https://m.blog.naver.com/PostView.nhn?blogId=kj930519&logNo=221440967773&proxyReferer=https:%2F%2Fwww.google.com%2F Current Date
// https://www.youtube.com/watch?v=zS8jYzLKirM&feature=share ListView

// Restaurant Icons from:
// Top in Town Pizza : https://twitter.com/topintownpizza
// Lee Yuan: https://www.brandrepstaging30.com
// A&W: https://play.google.com/store/apps/details?id=com.myelane2_aw&hl=ko
// 104sushi: http://toktokvan.com/104/
// zugba: http://www.zugba.com
// The Unfindable Bar: https://static.yellowpages.ca/ypca/ypui-6.30.0.0-20201014.1607/resources/images/no-logo.png
// 7Eleven: https://logos-download.com/2937-7-eleven-logo-download.html
// Subway: https://logos-download.com/1729-subway-logo-download.html
// Starbucks: https://logos-download.com/1819-starbucks-logo-download.html
// Tim-Hortons: https://logos-download.com/9183-tim-hortons-logo-download.html
// Pizza Hut: https://logos-download.com/3264-pizza-hut-logo-download.html
// Mcdonalds: https://www.freepnglogos.com/images/mcdonalds-png-logo-2775.html

/**
 * Displays all the lists of the restaurants in alphabetical order.
 * The most recent inspections of each restaurant would be shown.
 */

// shows when a restaurant is in your favorites

public class RestaurantList extends AppCompatActivity {

    ListView listView;
    RestaurantAdapter restaurantAdapter;
    Restaurant restaurant = new Restaurant();
    Inspection inspection = new Inspection();
    Button goToMap;
    PassingSearch passingSearch= PassingSearch.getInstance();
    ArrayList<RecentRestaurant> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        listView = findViewById(R.id.restaurantListView);
        addinglist();
        listView.setOnItemClickListener(
                (parent,view,position,id)->{
                    Intent intent = RestaurantDetailsUI.makeDetailIntent(RestaurantList.this,arrayList.get(position).getIndex());
                    //Toast.makeText(getApplicationContext(), ""+SingletonRestaurantManager.getInstance().get(position).getFavStatus(), Toast.LENGTH_SHORT).show();
                    comeFromForRD.getInstance().setSearchValue("List");
                    startActivity(intent);
                }
        );

        goToMap = (Button) findViewById(R.id.btnStartMap);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RestaurantList.this, MapsActivity.class);
                //pass the index of current restaurant
                //intent.putExtra("fromMap:",-1);
                RestaurantList.this.startActivityForResult(intent,1);

            }
        });
        //iteration 3 search
        //Tutorial from:https://youtu.be/CTvzoVtKoJ8

        SearchView searchView = findViewById(R.id.searchRL);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                passingSearch.setSearchValue(query);
                RestaurantList.this.restaurantAdapter.getFilter().filter(passingSearch.getSearchValue());
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //although in the email, prof Jack said, he will assume to see the result after press enter,
                //it is nicer to see the changes while user types
                passingSearch.setSearchValue(newText);
                RestaurantList.this.restaurantAdapter.getFilter().filter(passingSearch.getSearchValue());
                return false;
            }
        });
        if( comeFromForRD.getInstance().getSearchValue().equals("FAV")){
            searchView.setQuery(",1",false);
        }

        searchView.setQuery(passingSearch.getSearchValue(),false);
        //testing
        //Toast.makeText(getApplicationContext(), ""+passingSearch.getSearchValue(), Toast.LENGTH_SHORT).show();

    }
    //back button behavior
    //to exit the app
    //source from: https://stackoverflow.com/questions/21253303/exit-android-app-on-back-pressed
    @Override
    public void onBackPressed(){
        //when the calling activity is the map activity
        //we exit the app
        //otherwise we keep the original function
        if(getCallingActivity().getClassName().equals("com.example.projecti3.UI.MapsActivity")){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();

    }


    public void addinglist() {

        SingletonRestaurantManager manager = SingletonRestaurantManager.getInstance();
        List<Restaurant> sortedRestaurantList = manager.sortAlphabetically();

        Restaurant restaurant = new Restaurant();
        List<Inspection> list;
        list = restaurant.getAllInspectionList();
        arrayList = new ArrayList<>();
        String name;
        int image;
        int numIssues=0;
        int hazardIcon=0;
        String hazardLevel="";
        String date;
        int i=0;

        for (Restaurant sr : manager.getRestaurantManager()) {
            name = sr.getName();
            if (name.contains("A&W")) {
                image= R.drawable.pattulo;
            } else if (name.contains("Lee Yuen Seafood Restaurant")) {
                image = R.drawable.leeyuan;
            } else if (name.contains("Town Pizza")) {
                image = R.drawable.pizza;
            } else if (name.contains("104 Sushi & Co.")) {
                image = R.drawable.sushi;
            } else if (name.contains("Zugba Flame Grilled Chicken")) {
                image = R.drawable.zugba;
            } else if (name.contains("McDonald's")) {
                image = R.drawable.mcdonalds;
            } else if (name.contains("Tim Hortons")) {
                image = R.drawable.tim_hortons;
            } else if (name.contains("Starbucks")) {
                image = R.drawable.starbucks;
            } else if (name.contains("Pizza Hut")) {
                image = R.drawable.pizza_hut;
            } else if (name.contains("Subway")) {
                image = R.drawable.subway;
            } else if (name.contains("7-Eleven")) {
                image = R.drawable.seveneleven;
            } else {
                image = R.drawable.restaurantimage;
            }

            String recentDate = "" + sr.getLatestInspectionDate(list, sr.getTrackingNum());

            //get all the inspection of restaurant we are working on
            List<Inspection> orderedList=sr.getAllInspection();

            //helps make the inspection listn in the order from latest to the oldest
            helperOrdering(orderedList);
            if(orderedList.size()!=0){
                numIssues = orderedList.get(0).getNumIssues();
                if (orderedList.get(0).getHazardLevel().equals("Low")) {
                    hazardLevel = getString(R.string.lowHazard);
                    hazardIcon = R.drawable.greencircle;
                } else if (orderedList.get(0).getHazardLevel().equals("Moderate")) {
                    hazardLevel = getString(R.string.moderateHazard);
                    hazardIcon = R.drawable.orangecircle;
                } else if (orderedList.get(0).getHazardLevel().equals("High")) {
                    hazardLevel = getString(R.string.highHazard);
                    hazardIcon = R.drawable.redcircle;
                }
            }
            else {
                hazardLevel = getString(R.string.none);
                hazardIcon = 0;
            }
            Calendar cal = Calendar.getInstance();
            int year1 = cal.get(Calendar.YEAR);
            int month1 = cal.get(Calendar.MONTH) + 1;
            int day1 = cal.get(Calendar.DAY_OF_MONTH);
            String today = "" + year1 * 10000 + month1 * 100 + day1;


            int recent = sr.getLatestInspectionDate(list, sr.getTrackingNum());

            int year2 = recent / 10000;
            int month2 = (recent - year2 * 10000) / 100;
            int day2 = (recent - year2 * 10000 - month2 * 100);

            int diff = calcDate(today, recentDate);
            if (diff >= 0 && diff < 30) {
                date = (diff + getString(R.string.days));
            } else if (diff < 0) {
                date = getString(R.string.notAvailable);
            } else if (diff >= 30 && diff < 365) {
                date = (getMonth(month2) + " " + day2);
            } else {
                date = (getMonth(month2) + " " + year2);
            }

            arrayList.add(new RecentRestaurant(name, image, numIssues, hazardIcon, hazardLevel, date,SingletonRestaurantManager.getInstance().get(i).getFavStatus(),i));
            i++;
        }
        restaurantAdapter = new RestaurantAdapter(this, R.layout.list_row, arrayList);
        listView.setAdapter(restaurantAdapter);
    }

    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    private int calcDate(String today, String recentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        try {
            Date start = sdf.parse(recentDate);
            Date end = sdf.parse(today);
            long difference = (end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000);
            return (int) difference;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Intent makeIntent(Context context) {
        // switching activity from MapsActivity to RestaurantList
        return new Intent (context, RestaurantList.class);
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
}
