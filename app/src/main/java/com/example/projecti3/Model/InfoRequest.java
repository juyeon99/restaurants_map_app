package com.example.projecti3.Model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Extracts values from the url JSON file to Shared preferences
 * updateAvailable sends a boolean value if an update is available
 * time1 sends a value of the time since last update
 * downloadCSV gives a value for then the app is first launched
 */
public class InfoRequest extends AsyncTask<String, String, String> {

    public static final String RESTARAUNT_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";

    public static final String INSPECTION_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";



    public String requestUrl;
    private static boolean update;
    private StringBuilder sb;
    private String date;
    private Context mContext;
    private long time1;
    private Boolean updateAvailable;
    private String csvLink;
    private boolean downloadCSV;

    public InfoRequest(Context context, boolean downloadCSV) {
        mContext = context;
        this.downloadCSV = downloadCSV;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        //File csvFile = getCSVFileFromDownloads(mContext, "_itr1");
//        File csvFile = getCSVFileFromDownloads(mContext, "inspectionreports_itr1");
//        File csvFile2 = getCSVFileFromDownloads(mContext, "restaurants_itr1");
//        if(csvFile != null && csvFile.exists()) {
//            csvFile.delete();
//
//        }
//        if(csvFile2 != null && csvFile2.exists()) {
//            csvFile2.delete();
//        }

        //int zero = 0;
    }

    @Override
    protected String doInBackground(String... urls) {
        String uri = urls[0];
        requestUrl = uri;
        BufferedReader reader = null;
        //restoreData(uri);
//        if(uri.equals(INSPECTION_URL)) {
//            restoreData(uri);
//        } else if(uri.equals(RESTARAUNT_URL)){
//
//        }
        restoreData(uri);
        int count;
        try {
            URL url = new URL(uri);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonStringy = sb.toString();

            JSONObject jsonn = new JSONObject(jsonStringy);
            JSONArray someT = jsonn.getJSONObject("result").getJSONArray("resources");
            JSONObject jojo = someT.getJSONObject(0);
            String lastMod = jojo.getString("last_modified");
            String downloader = jojo.getString("original_url");
//            URL downloadUrl = new URL(download);
//            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//            Uri uri1 = Uri.parse(download);
//            DownloadManager.Request request = new DownloadManager.Request(uri1);
//            request.setTitle("Restarant/Inspection");
//            request.setDescription("Downloading");
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            String csvFile;
//            if(download.contains("inspection_reports")) {
//                csvFile = "inspectionreports_itr1";
//            } else {
//                csvFile = "restaurants_itr1";
//            }
//            request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, csvFile+".csv");
//            downloadManager.enqueue(request);

            Boolean timePassed = checkIfLimitPassedBetweenTimes(time1,System.currentTimeMillis(),20);
            long time2 = System.currentTimeMillis();
            if(uri == INSPECTION_URL && updateAvailable) timePassed = true ;
            if(timePassed) {
                if (date == null || date != lastMod) {

                    updateAvailable = true;
                    //time1 = System.currentTimeMillis();
                } else {
                    updateAvailable = false;
                }
                //updateAvailable = true;
            } else {
                updateAvailable = false;
            }
//            saveData("time1",Long.toString(time1), Long.toString(0));
//            saveData("updateAvailable",Boolean.toString(updateAvailable),Boolean.toString(true));
//            saveData("lastMod"+uri, lastMod, null);
            SaveState.getInstance(mContext).saveData("time1",Long.toString(time1));
            SaveState.getInstance(mContext).saveData("updateAvailable",Boolean.toString(updateAvailable));
//            SaveState.getInstance(mContext).saveData("lastMod"+uri, lastMod);
            if(uri.equals(INSPECTION_URL)) {
                SaveState.getInstance(mContext).saveData(SingletonInspectionManager.getInstance().getLastModifiedDateKey(), lastMod);
                SaveState.getInstance(mContext).saveData(SingletonInspectionManager.getInstance().getStoreDataKey(),downloader);
            } else if(uri.equals(RESTARAUNT_URL)){
                SaveState.getInstance(mContext).saveData(SingletonRestaurantManager.getInstance().getLastModifiedDateKey(), lastMod);
                SaveState.getInstance(mContext).saveData(SingletonRestaurantManager.getInstance().getStoreDataKey(),downloader);
                //saveData(, downloader, null);
            }

//            date = lastMod;
            //restoreData(uri);
//            saveData(uri,date, null);
            /**
             * Make two shared preferences, one for the first url, one for the second
             *
             * Maybe use the String url that gets passed in as they key.......
             *
             * Make if statement checking the value of the shared preference
             *
             * if it is null, change the value of it to the lastMod date
             * and download the csv
             * if it is not the same as the lastMod date then change it to lastMod date
             * and download the csv
             * if they are the same, dont change anything
             *
             *
             *
             */
            int zero = 0;
            //lastMod = someT.getString(22);
            //dateInstance.getInstance().setDate(lastMod);


            int one = 1;

            //Extract Date Modified
            //Extract the newest CSV
            //in a folder called raw there exists some other csv, that csv will be replaced with the new one
            //key name "url" under the "resources" key
            //Can repeat with other link as long as we do it with this one
            //Cuz I have a second one to do the same thing
            return downloader;
        } catch (Exception e) {
            return null;
        }

    }

    public int checkForMissing(Context context) {
        int count = 0;

        File[] allFiles = getFilesFromDownload(context);

        boolean containI = false;
        boolean containR = false;
        for (File file : allFiles) {
            String fileName = file.getName();

            containR = fileName.contains("restaurants_itr1");
            containI = fileName.contains("inspectionreports_itr1");


        }
        if (!containR) {
            count = 1;
        }
        if (!containI) {
            count = 2;
        }
        if (!containI && !containR ) {
            count = 3;
        }
        return count;
    }



    private void restoreData(String key) {
        if(key == INSPECTION_URL) {
            date = SaveState.getInstance(mContext).restoreData(SingletonInspectionManager.getInstance().getDownloadDateKey(),null);
            csvLink = SaveState.getInstance(mContext).restoreData(SingletonInspectionManager.getInstance().getStoreDataKey(),null);
        } else if (key == RESTARAUNT_URL) {
            date = SaveState.getInstance(mContext).restoreData(SingletonRestaurantManager.getInstance().getDownloadDateKey(),null);
            csvLink = SaveState.getInstance(mContext).restoreData(SingletonRestaurantManager.getInstance().getStoreDataKey(),null);
        }

        time1 = Long.parseLong((SaveState.getInstance(mContext).restoreData("time1", Long.toString(0))));
        updateAvailable = Boolean.parseBoolean(SaveState.getInstance(mContext).restoreData("updateAvailable", Boolean.toString(true) ));


        //return  mainStuff;
    }
    public void deleteAllCSVDups() {
        String restList = "restaurants_itr1";
        String inspecRep2 = "inspectionreports_itr1";
        //File csvFile = getCSVFileFromDownloads(mContext, restList);
        //File csvFile2 = getCSVFileFromDownloads(mContext, inspecRep2);

//        if(csvFile != null && csvFile.exists()) {
//            csvFile.delete();
//
//        }
//        if(csvFile2 != null && csvFile2.exists()) {
//            csvFile2.delete();
//        }
        String path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";
        File downloadFolder = new File(path);
        File[] downloadFolderFiles = downloadFolder.listFiles();
        for (File file : downloadFolderFiles) {
            String fileName = file.getName();
            if (fileName.contains("itr1")) {
                file.delete();
            }
        }
        int zero = 0;
    }
    public static File getCSVFileFromDownloads(Context context, String fileNeeded) {
        File[] downloadFolderFiles = getFilesFromDownload(context);

//        for (File file : downloadFolderFiles) {
//            String fileName = file.getName();
//            if (fileName.contains(fileNeeded)) {
//                return file;
//            }
//        }
        for(int i = 0; i < downloadFolderFiles.length; i++) {
            String fileName = downloadFolderFiles[i].getName();
            if(fileName.contains(fileNeeded)) {
                return downloadFolderFiles[i];
            }

        }
        return null;
    }
    private static File[] getFilesFromDownload(Context context) {
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(context, "Don't have necessary permissions", Toast.LENGTH_LONG).show();
            return null;
        }

        String path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";
        File downloadFolder = new File(path);
        File[] downloadFolderFiles = downloadFolder.listFiles();

        return downloadFolderFiles;
    }
    //...
    public boolean checkIfLimitPassedBetweenTimes(long time1, long time2, long limit) {
        long limitInHours = limit*60*60*1000;
        return time1 < (time2 - limitInHours);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);



        if(s != null && downloadCSV) {
//            if(requestUrl.equals(RESTARAUNT_URL)) {
//                restoreData(SingletonRestaurantManager.getInstance().getStoreDataKey());
//
//            } else if (requestUrl.equals(INSPECTION_URL)) {
//                restoreData(SingletonInspectionManager.getInstance().getStoreDataKey());
//            }

            DownloadRequest dr = new DownloadRequest(mContext);
            dr.downloadThings(s);

        }


    }


}
