package com.example.android.insulina.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InsulinaContract {

    private InsulinaContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.insulina";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INSULINA = "insulina";

    public static class InsulinaEntry implements BaseColumns {
        // Complete URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INSULINA);

        // The MIME type of the {@link #CONTENT_URI} for a list
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSULINA;

        // The MIME type of the {@link #CONTENT_URI} for a single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSULINA;

        //Table name
        public static final String TABLE_NAME = "insulina";

        // Column ID
        public static final String _ID = BaseColumns._ID;

        /*
        Column name
        Takes String, can not be left empty
         */
        public static final String COLUMN_INSULINA_NAME = "nazwa";

        /*
        Ammount of Insuline intake
        Integer, can not be left empty
         */
        public static final String COLUMN_INSULINA_INTAKE = "jednostki";

        /*
        Short description
        String, optional entry
         */
        public static final String COLUMN_INSULINA_DESCRIPTION = "opis";

        /*
        Ammount of glucose level 2h after eating
        Integer, optional entry
         */
        public static final String COLUMN_INSULINA_GLUCOSE_2H_LATER = "cukier";
    }
}
