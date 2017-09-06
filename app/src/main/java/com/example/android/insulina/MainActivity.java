package com.example.android.insulina;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.insulina.data.InsulinaContract;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView itemsList = (ListView) findViewById(R.id.listViewId);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fabId);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        // Define a projection
        String[] projection = {
                InsulinaContract.InsulinaEntry._ID,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER
        };

        Cursor cursor = getContentResolver().query(InsulinaContract.InsulinaEntry.CONTENT_URI, projection, null,null, null);

        TextView displayView = (TextView) findViewById(R.id.text_view_pet);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            displayView.setText("Number of rows in insuline database table: " + cursor.getCount());
            displayView.append("\n" + InsulinaContract.InsulinaEntry._ID + " - "
                    + InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME + " - "
                    + InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE + " - "
                    + InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION + " - "
                    + InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER
                    + "\n");

            // Figures out index of each column
            int idColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME);
            int intakeColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE);
            int descriptionColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION);
            int laterColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentIntake = cursor.getInt(intakeColumnIndex);
                String currentDescription = cursor.getString(descriptionColumnIndex);
                int currentLaterGlucose = cursor.getInt(laterColumnIndex);

                // Appends the TextView
                displayView.append("\n" + currentID + " - "
                        + currentName + " - "
                        + currentIntake + " - "
                        + currentDescription + " - "
                        + currentLaterGlucose);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
