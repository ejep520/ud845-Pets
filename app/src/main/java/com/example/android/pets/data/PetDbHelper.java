package com.example.android.pets.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * @see SQLiteOpenHelper
 */
public class PetDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = PetDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;

    public PetDbHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onOpen(@NonNull SQLiteDatabase db) {
        final String SQL_MASTER = "sqlite_master";
        final String MASTER_QUERY = "name==? and type==?";
        final String[] MASTER_KEY = {PetEntry.TABLE_NAME, "table"};
        if (!db.isReadOnly()) {
            Cursor cursor = db.query(
                    SQL_MASTER,
                    null,
                    MASTER_QUERY,
                    MASTER_KEY,
                    null,
                    null,
                    null);
            if (cursor.getCount() < 1) {
                createTable(db);
            }
            cursor.close();
        }
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        if ((oldVersion == newVersion) && (oldVersion == 1)) {
            db.execSQL(DROP_TABLE);
            createTable(db);
        } else {
            Log.d(LOG_TAG, "No upgrade path available at this time.");
        }
    }

    /**
     * Creates the table in PetEntry.TABLE_NAME in the provided database.
     * @param db The database being modified.
     * @see PetEntry
     */
    private void createTable(@NonNull SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + PetEntry.TABLE_NAME + " (" +
                PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                PetEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PetEntry.COLUMN_BREED + " TEXT, " +
                PetEntry.COLUMN_GENDER + " INTEGER, " +
                PetEntry.COLUMN_WEIGHT + " INTEGER);";
        db.execSQL(CREATE_TABLE);
    }
}