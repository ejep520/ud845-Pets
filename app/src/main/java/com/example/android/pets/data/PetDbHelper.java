package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.example.android.pets.data.PetContract.PetEntry;

public class PetDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;
    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + PetEntry.TABLE_NAME + " (" +
            PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            PetEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            PetEntry.COLUMN_BREED + " TEXT, " +
            PetEntry.COLUMN_GENDER + " INTEGER, " +
            PetEntry.COLUMN_WEIGHT + " INTEGER);";

    public PetDbHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(CREATE_TABLE);
    }
}