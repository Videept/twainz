package com.example.twainz;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper{

    private  static final String table_name = "Fav_table";
    private  static final String columnID = "ID";
    private  static final String column2 = "StationName";
    private  static final String column3 = "State";
    private  static final String drop_table = "DROP TABLE IF EXISTS "+ table_name;

    public Database(Context context){
        super(context, table_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + table_name + "("+columnID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ column2 +"TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(drop_table);
        onCreate(db);
    }

    public void insertData(String Station, boolean State)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(column2,Station);
        contentValues.put(column3,State);
        //insert into table
        db.insert(table_name, null, contentValues);
        db.close();
    }

    public void deleteData(String station)
    {
        SQLiteDatabase db = this.getWritableDatabase();
       // db.delete(table_name, columnID + "= '" + String.valueOf(uid) + "'", );
        String[] whereArgs = {station};
        db.delete(table_name, column2 + " = ?",whereArgs);
        db.close();
    }

}
