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

/**
 * Created by user on 05.09.2017.
 */

public class InsulinaProvider extends ContentProvider {

    // DB Helper object
    private InsulinaDbHelper mDbHelper;

    private static final int INSUL = 100;

    private static final int INSUL_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

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
