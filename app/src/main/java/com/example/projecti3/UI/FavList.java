package com.example.projecti3.UI;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projecti3.Model.PassingSearch;
import com.example.projecti3.Model.comeFromForRD;
import com.example.projecti3.R;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.projecti3.UI.DBAdapter.COL_FAV;
import static com.example.projecti3.UI.DBAdapter.COL_HAZARD;
import static com.example.projecti3.UI.DBAdapter.COL_ISSUES;
import static com.example.projecti3.UI.DBAdapter.COL_LATEST;
import static com.example.projecti3.UI.DBAdapter.COL_NAME;

public class FavList extends AppCompatActivity {
    ListView listView;
    FavAdapter favAdapter;

    Button goToMap;
    Button goToRestList;
    PassingSearch passingSearch = PassingSearch.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav);

        listView = findViewById(R.id.favListView);
        addinglist();
        listView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    closeDB();
                    Intent intent = RestaurantDetailsUI.makeDetailIntent(FavList.this, position);
                    startActivity(intent);
                }
        );



        goToMap = (Button) findViewById(R.id.MapsStart);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDB();
                Intent intent = new Intent(FavList.this, MapsActivity.class);
                //pass the index of current restaurant
                //intent.putExtra("fromMap:",-1);
                FavList.this.startActivityForResult(intent, 1);

            }
        });

        goToRestList = (Button) findViewById(R.id.AllListStart);
        goToRestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDB();
                Intent intent = new Intent(FavList.this, RestaurantList.class);
                FavList.this.startActivityForResult(intent, 1);
                comeFromForRD.getInstance().setSearchValue("FavList");
                //pass the index of current restaurant
                //intent.putExtra("fromMap:",-1);
            }
        });
        //iteration 3 search
        //Tutorial from:https://youtu.be/CTvzoVtKoJ8
        SearchView searchView = findViewById(R.id.searchFL);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                passingSearch.setSearchValue(query);
                FavList.this.favAdapter.getFilter().filter(passingSearch.getSearchValue());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //although in the email, prof Jack said, he will assume to see the result after press enter,
                //it is nicer to see the changes will user typing
                passingSearch.setSearchValue(newText);
                FavList.this.favAdapter.getFilter().filter(passingSearch.getSearchValue());
                return false;
            }
        });
        searchView.setQuery(passingSearch.getSearchValue(), false);
        //testing
        //Toast.makeText(getApplicationContext(), ""+passingSearch.getSearchValue(), Toast.LENGTH_SHORT).show();

    }

    //back button behavior
    //to exit the app
    //source from: https://stackoverflow.com/questions/21253303/exit-android-app-on-back-pressed
    @Override
    public void onBackPressed() {
        //when the calling activity is the map activity
        //we exit the app
        //otherwise we keep the original function
        if (getCallingActivity().getClassName().equals("com.example.projecti3.UI.MainActivity")) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();

    }

    private DBAdapter db;

    public void addinglist() {

        ArrayList<FavItem> arrayList = new ArrayList<>();
        String name;
        int image;
        int numIssues = 0;
        int hazardIcon = 0;
        String hazardLevel = "";
        String date;

        db = new DBAdapter(this);
        db.open();

        List<String> restName = new ArrayList<>();
        List<String> hazardLev = new ArrayList<>();
        List<Integer> NewLatest = new ArrayList<>();
        List<Integer> issues = new ArrayList<>();
        List<String> fav = new ArrayList<>();

        Cursor cursor = db.fetch();
        if (cursor.moveToFirst()) {
            do {
                restName.add(cursor.getString(COL_NAME));
                NewLatest.add(cursor.getInt(COL_LATEST));
                hazardLev.add(cursor.getString(COL_HAZARD));
                issues.add(cursor.getInt(COL_ISSUES));
                fav.add(cursor.getString(COL_FAV));

            } while (cursor.moveToNext());
        }
        cursor.close();

        for (int k = 0; k < restName.size(); k++) {
            name = restName.get(k);
            if (name.contains("A&W")) {
                image = R.drawable.pattulo;
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

            String recentDate = "" + NewLatest.get(k);

            //get all the inspection of restaurant we are working on
            String orderedList = hazardLev.get(k);
            Log.d("My Activity", "recent = " + recentDate + " & haz = " + orderedList);

            numIssues = issues.get(k);

            String favStat = fav.get(k);

            //helps make the inspection listn in the order from latest to the oldest
            if (orderedList != null) {
                if (orderedList.equals("Low")) {
                    hazardLevel = "Low";
                    hazardIcon = R.drawable.greencircle;
                } else if (orderedList.equals("Moderate")) {
                    hazardLevel = "Moderate";
                    hazardIcon = R.drawable.orangecircle;
                } else if (orderedList.equals("High")) {
                    hazardLevel = "High";
                    hazardIcon = R.drawable.redcircle;
                }
            } else {
                hazardLevel = "None";
                hazardIcon = 0;
            }
            Calendar cal = Calendar.getInstance();
            int year1 = cal.get(Calendar.YEAR);
            int month1 = cal.get(Calendar.MONTH) + 1;
            int day1 = cal.get(Calendar.DAY_OF_MONTH);
            String today = "" + year1 * 10000 + month1 * 100 + day1;

            int recent = NewLatest.get(k);

            int year2 = recent / 10000;
            int month2 = (recent - year2 * 10000) / 100;
            int day2 = (recent - year2 * 10000 - month2 * 100);

            int diff = calcDate(today, recentDate);
            if (diff >= 0 && diff < 30) {
                date = (diff + " days");
            } else if (diff < 0) {
                date = "N/A";
            } else if (diff >= 30 && diff < 365) {
                date = (getMonth(month2) + " " + day2);
            } else {
                date = (getMonth(month2) + " " + year2);
            }

            arrayList.add(new FavItem(name, image, numIssues, hazardIcon, hazardLevel, date, favStat));
        }
        favAdapter = new FavAdapter(this, R.layout.fav_layout, arrayList);
        listView.setAdapter(favAdapter);
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

    private void closeDB() {
        db.close();
    }
}
