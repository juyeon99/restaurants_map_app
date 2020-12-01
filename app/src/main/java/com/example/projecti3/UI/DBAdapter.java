package com.example.projecti3.UI;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.projecti3.Model.Inspection;

import java.util.ArrayList;
import java.util.List;

// SQLite Database
// used to store/update/change favorite list

public class DBAdapter {

    public static String TAG = "DBAdapter";

    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;

    public static final int COL_NAME = 1;
    public static final int COL_TRACKING = 2;
    public static final int COL_NUM = 3;
    public static final int COL_FAV = 4;
    public static final int COL_LATEST = 5;
    public static final int COL_ISSUES = 6;
    public static final int COL_HAZARD = 7;
    public static final int COL_INSP = 8;

    public static String KEY_NAME = "name";
    public static String KEY_TRACKING = "track";
    public static String KEY_NUM = "position";
    public static String FAV_STATUS = "status";
    public static String LATEST = "latest";
    public static String ISSUES = "issues";
    public static String HAZARD_LEVEL = "level";
    public static String INSP = "insp";

    public static String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_NAME, KEY_TRACKING, KEY_NUM, FAV_STATUS, LATEST, ISSUES, HAZARD_LEVEL, INSP};

    public static String DATABASE_TABLE = "favorites";
    public static String DATABASE_NAME = "Restaurants";

    public static int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE + " ("
                    + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_NAME + " text not null, "
                    + KEY_TRACKING + " text not null, "
                    + KEY_NUM + " text not null, "
                    + FAV_STATUS + " int not null, "
                    + LATEST + " date not null, "
                    + ISSUES + " int not null, "
                    + HAZARD_LEVEL + " level not null, "
                    + INSP + " insp not null);";

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper helper;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        helper = new DatabaseHelper(context);
    }

    public DBAdapter open() {
        db = helper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        helper.close();
    }

    public void insertRow(String Name, String trackingNum, int Pos, String status, int LD, int issues, String level, List<Inspection> insp){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, Name);
        initialValues.put(KEY_TRACKING, trackingNum);
        initialValues.put(KEY_NUM, Pos);
        initialValues.put(FAV_STATUS, status);
        initialValues.put(LATEST, LD);
        initialValues.put(ISSUES, issues);
        initialValues.put(HAZARD_LEVEL, level);
        initialValues.put(INSP, String.valueOf(insp));
        db.insert(DATABASE_TABLE, null, initialValues);
    }

    public void deleteRow(long rowID){
        String where = KEY_ROWID + "=" + rowID;
        //Log.d("My activity", "row is " + rowID);
        db.delete(DATABASE_TABLE, where, null);
    }

    public int getByTrackingNum(String tracking){
        List<String> dBTrackNum = new ArrayList<>();
        Cursor c = fetch();

        if (c.moveToFirst()){
            do {
                dBTrackNum.add(c.getString(COL_TRACKING));
            } while (c.moveToNext());
        }
        for(int i = 0; i < dBTrackNum.size(); i++) {
            if(dBTrackNum.get(i).equals(tracking)){
                return i + 1;
            }
        }
        return 0;
    }

    public void deleteALL(){
        Cursor c = getALLRows();
        int rowID = c.getColumnIndexOrThrow(KEY_ROWID);
        if(c.moveToFirst()){
            do {
                deleteRow(c.getInt(rowID));
            } while (c.moveToNext());
        }
        c.close();

        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + DATABASE_TABLE + "'");
    }

    private Cursor getALLRows() {
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS, where,
                null, null, null, null, null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void updateRow(long rowId, String name, String tracking, int position, String status, int latest, int issues, String level, List<Inspection> insp){
        String where = KEY_ROWID + "=" + rowId;
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_TRACKING, tracking);
        contentValues.put(KEY_NUM, position);
        contentValues.put(FAV_STATUS, status);
        contentValues.put(LATEST, latest);
        contentValues.put(ISSUES, issues);
        contentValues.put(HAZARD_LEVEL, level);
        contentValues.put(INSP, String.valueOf(insp));

        db.update(DATABASE_TABLE, contentValues, where, null);
    }

    public Cursor fetch() {
        if(DATABASE_TABLE != null) {
            Cursor cursor = this.db.query(DatabaseHelper.DATABASE_TABLE, new String[]{DatabaseHelper.KEY_ROWID,
                    DatabaseHelper.KEY_NAME, DatabaseHelper.KEY_TRACKING, DatabaseHelper.KEY_NUM, DatabaseHelper.FAV_STATUS, DatabaseHelper.LATEST,
                    DatabaseHelper.ISSUES, DatabaseHelper.HAZARD_LEVEL, DatabaseHelper.INSP}, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        }
        return null;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        public static String DATABASE_TABLE = "favorites";
        public static String DATABASE_NAME = "Restaurants";
        public static final String KEY_ROWID = "_id";

        public static String KEY_NAME = "name";
        public static String KEY_TRACKING = "track";
        public static String KEY_NUM = "position";
        public static String FAV_STATUS = "status";
        public static String LATEST = "latest";
        public static String ISSUES = "issues";
        public static String HAZARD_LEVEL = "level";
        public static String INSP = "insp";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
