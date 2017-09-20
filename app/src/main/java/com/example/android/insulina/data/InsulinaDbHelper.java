package com.example.android.insulina.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InsulinaDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "insulinka.db";

    private static final int DATABASE_VERSION = 1;

    public InsulinaDbHelper (Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    /*
    Creates a new Database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + InsulinaContract.InsulinaEntry.TABLE_NAME + " ("
                + InsulinaContract.InsulinaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME + " TEXT NOT NULL, "
                + InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE + " INTEGER NOT NULL, "
                + InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION + " TEXT, "
                + InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
