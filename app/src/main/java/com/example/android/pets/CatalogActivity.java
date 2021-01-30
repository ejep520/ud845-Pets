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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    private SQLiteDatabase mDb;
    private PetDbHelper mDbHelper = new PetDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mDb = mDbHelper.getReadableDatabase();
        Log.d(LOG_TAG, "DB Name: " + mDbHelper.getDatabaseName());

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                mDb.insert(PetEntry.TABLE_NAME,null, values);
                return true;
            case actionDeleteAllEntries:
                mDbHelper.onUpgrade(mDb, 1, 1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
