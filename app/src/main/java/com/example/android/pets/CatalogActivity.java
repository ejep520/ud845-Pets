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

// import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
// import android.content.CursorLoader;
import android.content.Intent;
// import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.android.pets.data.PetAdapter;
import com.example.android.pets.data.PetContract.PetEntry;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Locale mLOCALE;
    private ListView mListView;
    private final int PET_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mLOCALE = getResources().getConfiguration().getLocales().get(0);
        mListView = findViewById(R.id.list_view_pet);
        mListView.setEmptyView(findViewById(R.id.empty_view));


        LoaderManager.enableDebugLogging(true);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

        mListView.setAdapter(new PetAdapter(this, null));
        LoaderManager.getInstance(this).initLoader(PET_LOADER, null, this);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            Uri uri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
            intent.setData(uri);
            Log.d("OnItemClick", uri.toString());
            startActivity(intent);
        });

    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        final Uri returnUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
        long returnId = ContentUris.parseId(returnUri);
        AddPetResolution(returnId, this, mLOCALE);
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
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case actionDeleteAllEntries:
                int deletedRows = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
                Toast.makeText(
                        this,
                        String.format(
                            mLOCALE,
                            getString(R.string.pet_deletion_finished),
                            deletedRows),
                        Toast.LENGTH_SHORT)
                    .show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public static void AddPetResolution(long idLoc, Context context, Locale locale) {
        if (idLoc < 0) {
            Toast.makeText(context, R.string.pet_insertion_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, String.format(locale, context.getString(R.string.pet_inserted_successfully_format), idLoc), Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED
        };
        if (id == PET_LOADER) {
            return new CursorLoader(
                    this,
                    PetEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
        }
        throw new IllegalArgumentException("Unrecognized ID argument.");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ((PetAdapter)mListView.getAdapter()).changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ((PetAdapter)mListView.getAdapter()).changeCursor(null);
    }

}
