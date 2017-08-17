package com.example.android.insulina.data;

import android.provider.BaseColumns;

public final class InsulinaContract {

    private InsulinaContract() {}

    public static class InsulinaEntry implements BaseColumns {
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
