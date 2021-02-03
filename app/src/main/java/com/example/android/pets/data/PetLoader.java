package com.example.android.pets.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.CursorLoader;

import java.util.concurrent.Callable;

public class PetLoader extends CursorLoader {

    public PetLoader(@NonNull Context context, @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }



    private class uriFetch implements Callable<Cursor> {

        @Override
        public Cursor call() throws Exception {
            return null;
        }
    }
}
