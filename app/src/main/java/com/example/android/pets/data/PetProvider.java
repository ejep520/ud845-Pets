package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.android.pets.data.PetContract.PetEntry.CONTENT_AUTHORITY;
import static com.example.android.pets.data.PetContract.PetEntry.PATH_PETS;

/**
 * {@link ContentProvider} for the Pets app.
 */
public class PetProvider extends ContentProvider {

    private final static String LOG_TAG = PetProvider.class.getSimpleName();
    private PetDbHelper mDbHelper;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    static {
        matcher.addURI(CONTENT_AUTHORITY, PATH_PETS, PETS);
        matcher.addURI(CONTENT_AUTHORITY, PATH_PETS + "/#", PET_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch(matcher.match(uri)) {
            case PETS:
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        db.close();
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
