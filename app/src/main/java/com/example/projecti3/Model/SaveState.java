package com.example.projecti3.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 *  Saves valeus into shared pref
 */
public class SaveState {
    private static SaveState instance;

    private SharedPreferences sp;
    private Context context;

    private SaveState(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("restInspec", Context.MODE_PRIVATE);
    }

    public static SaveState getInstance(Context context) {
        if(instance == null) {

            instance = new SaveState(context);
        }
        return instance;
    }

    public void saveData( String key, String value) {
        if(sp == null || key == null) return;
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public String restoreData( String key, String defaultVal) {
        if(sp == null || key == null) return defaultVal;
        return sp.getString(key,defaultVal);
    }
}
