package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import static com.example.android.pets.data.PetContract.PetEntry.CONTENT_AUTHORITY;
import static com.example.android.pets.data.PetContract.PetEntry.PATH_PETS;
import static com.example.android.pets.data.PetContract.PetEntry;

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
        switch (matcher.match(uri)) {
            case PETS:
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if ((matcher.match(uri) == PETS) && (values != null)) {
            return insertPet(uri, values);
        }
        if (values == null) {
            return null;
        }
        throw new IllegalArgumentException("Insertion is not supported for " + uri.toString());
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int returnValue;
        switch (matcher.match(uri)) {
            case PETS:
                if (TextUtils.isEmpty(selection) && (selectionArgs == null)) {
                    returnValue = db.delete(PetEntry.TABLE_NAME, null, null);
                } else if (TextUtils.isEmpty(selection) ^ (selectionArgs == null)) {
                    db.close();
                    throw new IllegalArgumentException("selection and selectionArgs must both be filled or not null.");
                } else {
                    returnValue = db.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                }
                break;
            case PET_ID:
                if (TextUtils.isEmpty(selection) && (selectionArgs == null)) {
                    List<String> uriParts = uri.getPathSegments();
                    selectionArgs = new String[]{uriParts.get(uriParts.toArray().length - 1)};
                    returnValue = db.delete(PetEntry.TABLE_NAME, "_id=?", selectionArgs);
                } else {
                    db.close();
                    throw new IllegalArgumentException("If a pet ID is included, all other parameters must be null.");
                }
                break;
            default:
                db.close();
                throw new IllegalArgumentException("The uri provided could not be parsed. " + uri);
        }
        db.close();
        if (returnValue > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnValue;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int returnValue;
        switch (matcher.match(uri)) {
            case PETS:
                returnValue = db.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PET_ID:
                String whereClause = PetEntry._ID + "=?";
                String[] arguments = { String.valueOf(ContentUris.parseId(uri)) };
                if ((values != null) && (values.keySet().contains(PetEntry._ID))) {
                    values.remove(PetEntry._ID);
                }
                returnValue = db.update(PetEntry.TABLE_NAME, values, whereClause, arguments);
                break;
            default:
                Log.d(LOG_TAG, "This uri could not be matched. " + uri);
                returnValue = 0;
        }
        db.close();
        if (returnValue > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnValue;
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        long newRow;
        if (values == null) {
            newRow = -1;
            Log.d(LOG_TAG, "A null value was passed for the values.");
        } else if (values.keySet().isEmpty()) {
            newRow = -1;
            Log.d(LOG_TAG, "An empty values package was passed.");
        } else if (TextUtils.isEmpty(values.getAsString(PetEntry.COLUMN_PET_NAME))) {
            newRow = -1;
            Log.d(LOG_TAG, "Pets require names.");
        } else if (TextUtils.isEmpty(values.getAsString(PetEntry.COLUMN_PET_BREED))) {
            newRow = -1;
            Log.d(LOG_TAG, "Pets require a breed, or at least a species.");
        } else if (values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT) < 0) {
            newRow = -1;
            Log.d(LOG_TAG, "Pets must have a weight >= 0");
        } else if ((values.getAsInteger(PetEntry.COLUMN_PET_GENDER) < 0) ||
                (values.getAsInteger(PetEntry.COLUMN_PET_GENDER) > 2)) {
            newRow = -1;
            Log.d(LOG_TAG, "Invalid pet gender detected!");
        } else {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            newRow = db.insert(PetEntry.TABLE_NAME, null, values);
            db.close();
        }
        if (newRow > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, newRow);
    }

    protected void finalize() {
        mDbHelper.close();
    }
}
