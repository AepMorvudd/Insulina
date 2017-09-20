package com.example.android.insulina.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class InsulinaProvider extends ContentProvider {
    // Tag for the log messages
    public static final String LOG_TAG = InsulinaProvider.class.getSimpleName();

    // DB Helper object
    private InsulinaDbHelper mDbHelper;

    private static final int INSUL = 100;

    private static final int INSUL_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InsulinaContract.CONTENT_AUTHORITY, InsulinaContract.PATH_INSULINA, INSUL);
        sUriMatcher.addURI(InsulinaContract.CONTENT_AUTHORITY, InsulinaContract.PATH_INSULINA + "/#", INSUL_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InsulinaDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match) {

            // Queries the table directly
            // Cursor may contain multiple rows
            case INSUL:
                cursor = database.query(InsulinaContract.InsulinaEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            // Queries specified row of the table
            //
            // For every "?" in the selection, we need to have an element in the selection
            // arguments that will fill in the "?". Since we have 1 question mark in the
            // selection, we have 1 String in the selection arguments' String array.
            case INSUL_ID:
                selection = InsulinaContract.InsulinaEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(InsulinaContract.InsulinaEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
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
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case INSUL:
                return InsulinaContract.InsulinaEntry.CONTENT_LIST_TYPE;
            case INSUL_ID:
                return InsulinaContract.InsulinaEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INSUL:
                return insertEntry(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Get Writable DB
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch(match) {
            case INSUL:
                rowsDeleted = database.delete(InsulinaContract.InsulinaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INSUL_ID:
                selection = InsulinaContract.InsulinaEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InsulinaContract.InsulinaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case INSUL:
                return updateEntry(uri, values, selection, selectionArgs);
            case INSUL_ID:
                selection = InsulinaContract.InsulinaEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateEntry(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertEntry(Uri uri, ContentValues values) {
        // Sanity check for all the data inserted
        String name = values.getAsString(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME);
        if(name == null) {
            throw new IllegalArgumentException("Entry requires a name");
        }
        int insrtion = values.getAsInteger(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE);
        if(insrtion <= 0) {
            throw new IllegalArgumentException("Need to add insuline intake ammount");
        }
        int latrGluco = values.getAsInteger(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER);
        if(latrGluco <= -1) {
            throw new IllegalArgumentException("Need to add glucose level 2h later");
        }

        // Get writable DB
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert new Entry with give values
        long id = database.insert(InsulinaContract.InsulinaEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Update CursotrLoader with information that something has changed
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateEntry(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Sanity check for all the data inserted
        String name = values.getAsString(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME);
        if(name == null) {
            throw new IllegalArgumentException("Entry requires a name");
        }
        int insrtion = values.getAsInteger(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE);
        if(insrtion == 0) {
            throw new IllegalArgumentException("Need to add insuline intake ammount");
        }
        int latrGluco = values.getAsInteger(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER);
        if(latrGluco <= -1) {
            throw new IllegalArgumentException("Need to add glucose level 2h later");
        }

        // Gets writable DB
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int id = database.update(InsulinaContract.InsulinaEntry.TABLE_NAME, values, selection, selectionArgs);

        // Update CursotrLoader with information that something has changed
        getContext().getContentResolver().notifyChange(uri, null);

        return id;
    }
}
