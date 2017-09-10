package com.example.android.insulina;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.insulina.data.InsulinaContract;

public class InsulinaCursorAdapter extends CursorAdapter {

    public InsulinaCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    // The newView method is used to inflate a new view and return it.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_detail, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate
        TextView displayName = (TextView) view.findViewById(R.id.display_name);
        TextView displayIntake = (TextView) view.findViewById(R.id.display_intake);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME));
        String intake = cursor.getString(cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE));
        // Populate fields
        displayName.setText(name);
        displayIntake.setText(intake);
    }
}
