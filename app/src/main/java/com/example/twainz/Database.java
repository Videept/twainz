package com.example.twainz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.net.IDN;
import java.security.cert.CRLReason;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    private static final String database_name = "fav_database.db";

    private static final String table_name = "Fav_table";
    private static final String columnID = "ID";
    private static final String column2 = "StationName";
    private static final String drop_table = "DROP TABLE IF EXISTS " + table_name;

    public Database(Context context) {
        super(context, database_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + table_name + "(" + columnID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + column2 + " TEXT)";

        db.execSQL(createTable);
    }

public ArrayList<String> displayfavourites(){
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ table_name,null);
        cursor.moveToFirst();
        while(cursor.moveToLast()==false){
            array_list.add(cursor.getString(cursor.getColumnIndex(column2)));
            cursor.moveToNext();
        }
        return array_list;
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(drop_table);
        onCreate(db);
    }

    public void insertData(String Station) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }


    public void deleteData(String station) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = {station};
        db.delete(table_name, column2 + " = ?", whereArgs);
        db.close();
    }

    public ArrayList<String> getFavouritesList() {
        //used to rebuild all notes on startup.
        //returns arraylist of all strings
        SQLiteDatabase database = this.getReadableDatabase();
        long row_count = DatabaseUtils.queryNumEntries(database, table_name);
        //database.close();
        Log.d("d_tag", String.valueOf(row_count) + " is size of db");

        //SQLiteDatabase database =this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + table_name, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    list.add(cursor.getString(1));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("d_tag", e.toString());
        }

        cursor.close();
        database.close();
        return list;
    }


}