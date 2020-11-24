package com.example.projecti3.Model;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

/**
 * Downloads from the .csv link
 */
public class DownloadRequest {

    public static final String RESTARAUNT_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    public static final String INSPECTION_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";


    private Context mContext;
    private String date;
    private String lastMod;
    private SharedPreferences sp;
    private DownloadManager downloadManager;
    private long downloadID = -1;
    public DownloadRequest(Context context) {
        mContext = context;
        sp = mContext.getSharedPreferences("restInspec", Context.MODE_PRIVATE);
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }
    public void downloadThings(String downloadUrl) {

        Uri uri1 = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Application Data");
        request.setDescription("Downloading");
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String csvFile;

        if(downloadUrl.contains("inspection")) {
            csvFile = "inspectionreports_itr1";
            restoreData(csvFile);
            date = lastMod;
            SaveState.getInstance(mContext).saveData(SingletonInspectionManager.getInstance().getDownloadDateKey(), date);

        } else {
            csvFile = "restaurants_itr1";
            restoreData(csvFile);
            date = lastMod;
            SaveState.getInstance(mContext).saveData(SingletonRestaurantManager.getInstance().getDownloadDateKey(),date);

        }
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, csvFile+".csv");
        if(downloadManager != null) {
            downloadID = downloadManager.enqueue(request);
        }


    }

    public void cancelDownload() {

        if(downloadID != -1) {

            downloadManager.remove(downloadID);
        }
    }

    private void restoreData(String key) {
        if(key.equals("restaurants_itr1")) {
            date = SaveState.getInstance(mContext).restoreData(SingletonRestaurantManager.getInstance().getDownloadDateKey(),null);
            lastMod = SaveState.getInstance(mContext).restoreData(SingletonRestaurantManager.getInstance().getLastModifiedDateKey(),null);
        } else if (key.equals("inspectionreports_itr1")) {
            date = SaveState.getInstance(mContext).restoreData(SingletonInspectionManager.getInstance().getDownloadDateKey(),null);
            lastMod = SaveState.getInstance(mContext).restoreData(SingletonInspectionManager.getInstance().getLastModifiedDateKey(),null);
        }
    }
}
