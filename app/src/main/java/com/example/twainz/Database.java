package com.example.twainz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    array_list.add(cursor.getString(1));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
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

        ContentValues contentValues = new ContentValues();
        contentValues.put(column2, Station);
        //insert into table
        db.insert(table_name, null, contentValues);
        db.close();
    }


    public void deleteData(String station) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = {station};
        db.delete(table_name, column2 + " = ?", whereArgs);
        db.close();
    }


}