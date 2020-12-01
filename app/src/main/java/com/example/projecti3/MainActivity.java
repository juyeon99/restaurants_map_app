package com.example.projecti3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projecti3.Model.DownloadRequest;
import com.example.projecti3.Model.InfoRequest;
import com.example.projecti3.Model.Inspection;
import com.example.projecti3.Model.Restaurant;
import com.example.projecti3.Model.SaveState;
import com.example.projecti3.Model.SingletonInspectionManager;
import com.example.projecti3.Model.SingletonRestaurantManager;
import com.example.projecti3.Model.comeFromForRD;
import com.example.projecti3.UI.DBAdapter;
import com.example.projecti3.UI.MapsActivity;
import com.example.projecti3.UI.RestaurantList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.projecti3.UI.DBAdapter.COL_LATEST;
import static com.example.projecti3.UI.DBAdapter.COL_TRACKING;

/**
 * Main activity --> displays welcome screen and then after 3.5 seconds it automatically switches to RestaurantList
 * generates data from csv files and stores them
 * shows when an update is available
 * if favorite restaurant got updated it tells you
 */

public class MainActivity extends AppCompatActivity {
    public Handler handler;
    protected ProgressDialog progressDialog;
    protected ProgressDialog pD;

    private Timer timer;
    private long tStart;
    private int counter = 0;
    boolean bufferRest = false;

    private String downloadRestarauntCSVLink;
    private String downloadInspectionCSVLink;

    private Boolean availableUpdate;
    private Boolean firstRun;

    private DownloadRequest dr1;
    private DownloadRequest dr2;

    Inspection inspection;
    Restaurant restaurant;
    SingletonRestaurantManager manager;
    SingletonInspectionManager inspectionManager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO
        restoreData();
        getJSONData(false,0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermissions();
        handler = new Handler();
        //Time to launch the another activity
        int TIME_OUT = 1000;
        //availableUpdate = Boolean.parseBoolean(restoreData("updateAvailable", Boolean.toString(true)));
        if (availableUpdate) {
            handler.postDelayed(this::checkForUpdate, TIME_OUT);
        } else {
            fixMissingDownloads();
            handler.postDelayed(this::startNextActivity, TIME_OUT);

        }
        String loadingData = getApplication().getResources().getString(R.string.loadingData);
        String pleaseWait = getApplicationContext().getResources().getString(R.string.pleaseWait);
        pD = new ProgressDialog(MainActivity.this);
        pD.setTitle(loadingData);
        pD.setMessage( pleaseWait + "...");
        pD.show();
        //handler.postDelayed(this::checkForUpdate, TIME_OUT);
    }

    private void fixMissingDownloads() {
        InfoRequest irFixer;
        InfoRequest irFixer2;
        irFixer =  new InfoRequest(getApplicationContext(), true);
        irFixer2 = new InfoRequest(getApplicationContext(),true);
        //irFixer
        int howManyMissing = irFixer.checkForMissing(getApplicationContext());
        if(howManyMissing == 1) {
            irFixer.execute(InfoRequest.RESTARAUNT_URL);
        } else if(howManyMissing == 2) {
            irFixer.execute(InfoRequest.INSPECTION_URL);
        } else if (howManyMissing == 3) {
            irFixer.execute(InfoRequest.RESTARAUNT_URL);
            irFixer2.execute(InfoRequest.INSPECTION_URL);
        }
    }


    private void getJSONData(boolean downloadCSV, int chosenDownload) {
        if(downloadCSV) {
            if(chosenDownload == 1) {
                InfoRequest ir = new InfoRequest(getApplicationContext(), downloadCSV);
                ir.execute(InfoRequest.RESTARAUNT_URL);
            } else {
                InfoRequest ir2 = new InfoRequest(getApplicationContext(), downloadCSV);
                ir2.execute(InfoRequest.INSPECTION_URL);
            }
        } else {
            InfoRequest ir = new InfoRequest(getApplicationContext(), downloadCSV);
            ir.execute(InfoRequest.RESTARAUNT_URL);
            InfoRequest ir2 = new InfoRequest(getApplicationContext(), downloadCSV);
            ir2.execute(InfoRequest.INSPECTION_URL);
        }


    }

    private void checkForUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String newUpdate = getApplicationContext().getResources().getString(R.string.newUpdate);
        String wantUpdate = getApplicationContext().getResources().getString(R.string.wantUpdate);
        String yes = getApplicationContext().getResources().getString(R.string.yes);
        String no = getApplicationContext().getResources().getString(R.string.no);
        builder.setTitle( newUpdate + "!!");
        builder.setMessage(wantUpdate);
        dr1 = new DownloadRequest(getApplicationContext());
        dr2 = new DownloadRequest(getApplicationContext());
        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bufferRest = true;
                dialog.cancel();
                dialog.dismiss();
                InfoRequest irDeleteCSV = new InfoRequest(getApplicationContext(), false);
                irDeleteCSV.deleteAllCSVDups();
                if (downloadRestarauntCSVLink != null) {

                    dr1.downloadThings(downloadRestarauntCSVLink);
                } else {

                    getJSONData(true, 1);
                }
                if (downloadInspectionCSVLink != null) {

                    dr2.downloadThings(downloadInspectionCSVLink);
                } else {

                    getJSONData(true, 2);
                }
                SaveState.getInstance(getApplicationContext()).saveData("time1", Long.toString(System.currentTimeMillis()));
//                //getJSONData();
//
//
                SaveState.getInstance(getApplicationContext()).saveData("updateAvailable", Boolean.toString(false));
                update();
            }
        });

        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
                startNextActivity();
                if (dr1 != null) dr1.cancelDownload();
                if (dr2 != null) dr2.cancelDownload();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void update() {
        String downloadingData = getApplicationContext().getResources().getString(R.string.downloadingData);
        String pleaseWait = getApplicationContext().getResources().getString(R.string.pleaseWait);
        String cancelUpdate = getApplicationContext().getResources().getString(R.string.cancelUpdate);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle(downloadingData);
        progressDialog.setMessage(pleaseWait + "...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelUpdate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bufferRest = false;
                dr1.cancelDownload();
                dr2.cancelDownload();
                progressDialog.cancel();
                progressDialog.dismiss();
                counter = 0;
                timer.cancel();
                startNextActivity();
            }
        });
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        tStart = System.currentTimeMillis();

        progress();
    }

    private void progress() {
        timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if (counter == 100) {
                    timer.cancel();
                    progressDialog.dismiss();
                    progressDialog.cancel();
                    startNextActivity();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        counter++;
                        progressDialog.setProgress(counter);
                        long tEnd = System.currentTimeMillis();
                        long tDelta = tEnd - tStart;
                        double elapsedSeconds = tDelta / 1000.0;
                        int speed = (int) (counter / elapsedSeconds);
                        String speedOfDownload = getApplicationContext().getResources().getString(R.string.downloadSpeed);
                        String perSecond = getApplicationContext().getResources().getString(R.string.perSecond);
                        progressDialog.setMessage( speedOfDownload+ ": " + speed + "MB " + perSecond);
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 30);
    }

    public void startNextActivity() {
        readRestaurantList();
        readInspectionList();

        updateDB();

        if(pD != null && pD.isShowing()) {
            pD.dismiss();
            pD.cancel();
        }

        if(needsUpdateFav){
            favUpdated();
        }
        else {
            toMaps();
        }
    }

    public void toMaps(){
        if(alertD != null && alertD.isShowing()){
            alertD.dismiss();
            alertD.cancel();
        }
        if(db != null) {
            closeDB();
        }
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    public void toFavList(){
        if(alertD != null && alertD.isShowing()){
            alertD.dismiss();
            alertD.cancel();
        }
        if(db != null) {
            closeDB();
        }
        Intent intent = new Intent(MainActivity.this, RestaurantList.class);
        comeFromForRD.getInstance().setSearchValue("FAV");
        startActivity(intent);
        finish();
    }

    private BufferedReader BufferInsp() {
        BufferedReader reader = null;

        try {
            File csvFile = InfoRequest.getCSVFileFromDownloads(getApplicationContext(), "inspectionreports_itr1");
            if (csvFile == null ) {
                InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            } else {
                FileReader file = new FileReader(csvFile.getAbsolutePath());
                reader = new BufferedReader(file);

            }
            return reader;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return reader;
    }

    private BufferedReader BufferRest() {
        BufferedReader reader = null;
        try {
            File csvFile = InfoRequest.getCSVFileFromDownloads(getApplicationContext(), "restaurants_itr1");
            if (csvFile == null ) {
                InputStream is = getResources().openRawResource(R.raw.restaurants_itr1);
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            } else {
                FileReader file = new FileReader(csvFile.getAbsolutePath());
                reader = new BufferedReader(file);

            }
            return reader;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return reader;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readInspectionList() {
        int count = 0;
        String line = "";
        try {
            BufferedReader reader = BufferInsp();

            String text = reader.readLine();
            text = text.replace("\"", "");
            String[] headers = text.split(",");
            int trackingNum = 0, date = 0, inspType = 0, crit = 0, nonCrit = 0, hazard = 0, violation = 0;

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].toUpperCase().equals("TRACKINGNUMBER")) {
                    trackingNum = i;
                } else if (headers[i].toUpperCase().equals("INSPECTIONDATE")) {
                    date = i;
                } else if (headers[i].toUpperCase().equals("INSPTYPE")) {
                    inspType = i;
                } else if (headers[i].toUpperCase().equals("NUMCRITICAL")) {
                    crit = i;
                } else if (headers[i].toUpperCase().equals("NUMNONCRITICAL")) {
                    nonCrit = i;
                } else if (headers[i].toUpperCase().equals("HAZARDRATING")) {
                    hazard = i;
                } else if (headers[i].toUpperCase().equals("VIOLLUMP")) {
                    violation = i;
                }
            }
            int zero = 0;
            while ((line = reader.readLine()) != null && line.split(",").length != 0) {
                //line.replaceAll("\"" , "");
                String[] token = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (token.length < 5 || token.length > 7) {
                    //something is wrong with this line
                    //unsuccessful download ????
                    Log.e("My activity", "Error reading data file on line " + line);
                    continue;
                }

                //Log.d("My activity", count + " Error???" + Arrays.toString(token));

                inspectionManager = SingletonInspectionManager.getInstance();

                inspection = new Inspection();
                if(token.length > trackingNum) {
                    inspection.setTrackingNum(token[trackingNum].replaceAll("\"", ""));
                }
                if(token.length > date && token[date].matches("-?\\d+")) {
                    inspection.setDate(Integer.parseInt(token[date]));
                }
                if(token.length > inspType) {
                    inspection.setType(token[inspType].replaceAll("\"", ""));
                }

                if(token.length > crit && token[crit].matches("-?\\d+")) {
                    inspection.setCritIssues(Integer.parseInt(token[crit]));
                }

                if(token.length > nonCrit && token[nonCrit].matches("-?\\d+")) {
                    inspection.setNoncritIssuses(Integer.parseInt(token[nonCrit]));
                }

                if (hazard < token.length) {
                    inspection.setHazardLevel(token[hazard].replaceAll("\"", ""));

                } else {
                    inspection.setHazardLevel("");
                }
                if (violation < token.length) {
                    String violationForInspection = token[violation].replace("\"", "");
                    inspection.setViolations(violationForInspection);
                    //Log.d("My activity", count + "Violations: "  + violationForInspection);
                } else {
                    inspection.setViolations("");
                }

                inspection.setNumIssues();
                inspectionManager.add(inspection);

                count++;
            }
            reader.close();
        } catch (IOException e) {
            Log.wtf("My activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

        List<Restaurant> peakListTwo = manager.getAll();
        List<Inspection> peakListOne = inspectionManager.getAll();

        if(peakListOne != null && peakListTwo != null){
            int c = 0;
            for(Inspection peak : peakListOne){
                for(Restaurant peak2 : peakListTwo){
                    if((peak2.getTrackingNum().equals(peak.getTrackingNum()))){
                        peak.setInspectionIndex(c);
                        peak2.addInspection(peak, peak2.getTrackingNum());
                    }
                }
                c++;
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readRestaurantList() {
        String line = "";
        try {
            BufferedReader reader = BufferRest();

            String text = reader.readLine();
            text = text.replace("\"", "");
            String[] headers = text.split(",");

            int trackingNum = 0, name = 0, address = 0, latitude = 0, longitude = 0;

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].toUpperCase().equals("TRACKINGNUMBER")) {
                    trackingNum = i;
                } else if (headers[i].toUpperCase().equals("NAME")) {
                    name = i;
                } else if (headers[i].toUpperCase().equals("PHYSICALADDRESS")) {
                    address = i;
                } else if (headers[i].toUpperCase().equals("LATITUDE")) {
                    latitude = i;
                } else if (headers[i].toUpperCase().equals("LONGITUDE")) {
                    longitude = i;
                }
            }

            //Log.d("My activity", " Error???" + Arrays.toString(headers));


            while ((line = reader.readLine()) != null) {
                line.replaceAll("\"","");
                String[] token = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                manager = SingletonRestaurantManager.getInstance();

                restaurant = new Restaurant();

                restaurant.setFavStatus("0");

                if(token.length > trackingNum) {
                    restaurant.setTrackingNum(token[trackingNum].replaceAll("\"", ""));
                }
                if(token.length > name) {
                    restaurant.setName(token[name].replaceAll("\"", ""));
                }

                if(token.length > address) {
                    restaurant.setAddress(token[address].replaceAll("\"", ""));
                }

                if(token.length > latitude && token[latitude].matches("[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)")) {
                    restaurant.setLatitude(Float.parseFloat(token[latitude]));
                }
                if(token.length > longitude && token[longitude].matches("[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)")) {
                    restaurant.setLongitude(Float.parseFloat(token[longitude]));
                }

                manager.add(restaurant);
            }
            reader.close();
        } catch (IOException e) {
            Log.wtf("My activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    private void restoreData() {
        firstRun = Boolean.parseBoolean(SaveState.getInstance(getApplicationContext()).restoreData("firstRun", Boolean.toString(false)));
        downloadRestarauntCSVLink = SaveState.getInstance(getApplicationContext()).restoreData(SingletonRestaurantManager.getInstance().getStoreDataKey(),null );
        downloadInspectionCSVLink = SaveState.getInstance(getApplicationContext()).restoreData(SingletonInspectionManager.getInstance().getStoreDataKey(),null );
        availableUpdate = Boolean.parseBoolean(SaveState.getInstance(getApplicationContext()).restoreData("updateAvailable",Boolean.toString(true)));
        //String returnValue = SaveState.getInstance(getApplicationContext()).restoreData(key,defVal);
    }

    private DBAdapter db;
    private AlertDialog alertD;
    List<Integer> lastTimeLatest;
    private boolean needsUpdateFav = false;

    private void openDB() {
        db = new DBAdapter(this);
        db.open();
    }

    private void updateDB(){

        List<String> favTrackingNum = new ArrayList<>();
        lastTimeLatest = new ArrayList<>();

        SingletonRestaurantManager manager = SingletonRestaurantManager.getInstance();
        List<Restaurant> myRestaurants = manager.getAll();

        openDB();

        Cursor cursor = db.fetch();
        if (cursor.moveToFirst()) {
            do {
                favTrackingNum.add(cursor.getString(COL_TRACKING));
                lastTimeLatest.add(cursor.getInt(COL_LATEST));

            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDB();

        openDB();
        db.deleteALL();

        //Log.d("My Activity", "fav size " + favTrackingNum.size());

        for(int k = 0; k < favTrackingNum.size(); k++) {
            for (int j = 0; j < myRestaurants.size(); j++) {
                if (favTrackingNum.get(k).equals(myRestaurants.get(j).getTrackingNum())) {
                    myRestaurants.get(j).setFavStatus("1");
                    //Log.d("My Activity", "status " + k + " == " + myRestaurants.get(j).getFavStatus());

                    openDB();

                    db.insertRow( myRestaurants.get(j).getName(), myRestaurants.get(j).getTrackingNum(), j, myRestaurants.get(j).getFavStatus(),
                            myRestaurants.get(j).getLatestInspectionDate(myRestaurants.get(j).getAllInspection(), myRestaurants.get(j).getTrackingNum()),
                            myRestaurants.get(j).getLatestNumIssues(), myRestaurants.get(j).getLatestHazard(), myRestaurants.get(j).getAllInspection());

                   /* Log.d("My Activity", "row is " +" " + favTrackingNum.get(k));
                    Log.d("My Activity", "info is " + myRestaurants.get(j).getName() + " " + myRestaurants.get(j).getTrackingNum() + " " + j
                            + " " + myRestaurants.get(j).getFavStatus() + " " + myRestaurants.get(j).getLatestInspectionDate(myRestaurants.get(j).getAllInspection(), myRestaurants.get(j).getTrackingNum())
                            + " " + myRestaurants.get(j).getLatestNumIssues() + " " + myRestaurants.get(j).getLatestHazard() + " " + myRestaurants.get(j).getAllInspection());


                    Log.d("My Activity", "row updated ");*/

                    closeDB();

                    if (lastTimeLatest.get(k) != myRestaurants.get(j).getLatestInspectionDate(myRestaurants.get(j).getAllInspection(), myRestaurants.get(j).getTrackingNum())) {
                        //Log.d("My Activity", "latest compare " + (lastTimeLatest.get(k)) + " == " + myRestaurants.get(j).getLatestInspectionDate(myRestaurants.get(j).getAllInspection(), myRestaurants.get(j).getTrackingNum()));
                        needsUpdateFav = true;
                    }
                    //Log.d("My Activity", "latest " + myRestaurants.get(j).getLatestInspectionDate(myRestaurants.get(j).getAllInspection(), myRestaurants.get(j).getTrackingNum()));
                }
            }
        }
    }

    private void closeDB() {
        db.close();
    }

    private void favUpdated() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        String favsUpdate = getApplicationContext().getResources().getString(R.string.favsUpdate);

        String yes = "Yes";
        String no = "No";

        alert.setTitle( favsUpdate + "!!");
        alert.setMessage(R.string.seeFavs_);

        alert.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
                dialogInterface.dismiss();
                alertD.dismiss();
                alertD.cancel();
                toFavList();
            }
        });

        alert.setNegativeButton(no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
                dialogInterface.dismiss();
                alertD.dismiss();
                alertD.cancel();
                toMaps();
            }
        });
        runOnUiThread(new Runnable() {
            public void run() {
                alertD = alert.create();
                alertD.show();
            }
        });
    }

    private void requestPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, 1000);
        }
    }

    // Need Storage Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "Cannot download data without permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db != null) {
            closeDB();
        }
        if(alertD != null && alertD.isShowing()){
            alertD.dismiss();
            alertD.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(db != null) {
            closeDB();
        }
        if(alertD != null && alertD.isShowing()){
            alertD.dismiss();
            alertD.cancel();
        }
    }
}
