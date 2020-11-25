package com.example.projecti3.UI;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    public static String TAG = "DBAdapter";

    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;

    public static final int COL_NAME = 1;
    public static final int COL_NUM = 2;
    public static final int COL_FAV = 3;
    public static final int COL_LATEST = 4;

    public static String KEY_NAME = "name";
    public static String KEY_NUM = "position";
    public static String FAV_STATUS = "status";
    public static String LATEST = "latest";

    public static String[] ALL_KEYS = new String[] {KEY_NAME, KEY_NUM, FAV_STATUS, LATEST};

    public static String DATABASE_TABLE = "favorites";
    public static String DATABASE_NAME = "Restaurants";

    public static int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE + " ("
                    + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_NAME + " text not null, "
                    + KEY_NUM + " text not null, "
                    + FAV_STATUS + " int not null, "
                    + LATEST + " date not null);";

    private SQLiteDatabase db;
    private final Context context;
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

    public long insertRow(String name, int position, String favStatus, int latest){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_NUM, position);
        initialValues.put(FAV_STATUS, favStatus);
        initialValues.put(LATEST, latest);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public void deleteRow(long rowID){
        String where = KEY_ROWID + "=" + rowID;
        db.delete(DATABASE_TABLE, where, null);
    }

    public int getByName(String name){
        Cursor c = getALLRows();
        int i = 0;
        if (c.moveToFirst()){
            do {
                if(KEY_NAME.equals(name)){
                    break;
                }
                i++;
            } while (c.moveToNext());
        }
        return i;
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
        String where = KEY_NUM + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public boolean updateRow(long rowID, String name, int position, String status, int latest){
        String where = KEY_ROWID + "=" + rowID;
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_NUM, position);
        contentValues.put(FAV_STATUS, status);
        contentValues.put(LATEST, latest);

        return db.update(DATABASE_TABLE, contentValues, where, null) != 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
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
