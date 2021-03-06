/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.IllegalFormatException;
import java.util.Locale;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    private SQLiteDatabase mDb;
    private PetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDbHelper = new PetDbHelper(this);
        mDb = mDbHelper.getWritableDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int actionInsertDummyData = R.id.action_insert_dummy_data;
        final int actionDeleteAllEntries = R.id.action_delete_all_entries;
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case actionInsertDummyData:
                ContentValues values = new ContentValues();
                values.put(PetEntry.COLUMN_NAME, "Garfield");
                values.put(PetEntry.COLUMN_BREED, "Tabby");
                values.put(PetEntry.COLUMN_GENDER, PetEntry.GENDER_MALE);
                values.put(PetEntry.COLUMN_WEIGHT, 7);
                if (mDb.insert(PetEntry.TABLE_NAME, null, values) < 0) {
                    Log.d(LOG_TAG, "An error occurred while inserting the row.");
                }
                displayDatabaseInfo();
                return true;
            case actionDeleteAllEntries:
                mDbHelper.onUpgrade(mDb, 1, 1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayDatabaseInfo() {
        final String divider = " - ";
        final StringBuilder sb = new StringBuilder(PetEntry.COLUMN_NAME + divider +
                PetEntry.COLUMN_BREED + divider + PetEntry.COLUMN_GENDER + divider +
                PetEntry.COLUMN_WEIGHT + "\n");
        final Locale locale = getResources().getConfiguration().getLocales().get(0);
        final TextView displayView = findViewById(R.id.text_view_pet);
        Cursor cursor = mDb.query(
                PetEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        try {
            displayView.setText(String.format(locale, "Number of rows in pets DB table: %d\n\n", cursor.getCount()));
        } catch (IllegalFormatException err) {
            Log.d(LOG_TAG, err.getLocalizedMessage());
            err.printStackTrace();
        }
        cursor.moveToPosition(-1);
        final int namePos = cursor.getColumnIndex(PetEntry.COLUMN_NAME);
        final int breedPos = cursor.getColumnIndex(PetEntry.COLUMN_BREED);
        final int genderPos = cursor.getColumnIndex(PetEntry.COLUMN_GENDER);
        final int weightPos = cursor.getColumnIndex(PetEntry.COLUMN_WEIGHT);
        while (cursor.moveToNext()) {
            sb.append("\n").append(cursor.getString(namePos)).append(divider);
            sb.append(cursor.getString(breedPos)).append(divider);
            switch (cursor.getInt(genderPos)) {
                case PetEntry.GENDER_MALE:
                    sb.append("Male").append(divider);
                    break;
                case PetEntry.GENDER_FEMALE:
                    sb.append("Female").append(divider);
                    break;
                default:
                    sb.append("Weird").append(divider);
                    break;
            }
            sb.append(cursor.getInt(weightPos));
        }
        displayView.append(sb.toString());
        cursor.close();
    }

    // If you open it, close it. You probably weren't raised in a barn. If you were, refer to the first sentence.
    @Override
    protected void onStop() {
        mDb.close();
        mDbHelper.close();
        super.onStop();
    }
}
