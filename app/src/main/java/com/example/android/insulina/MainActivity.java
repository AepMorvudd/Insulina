package com.example.android.insulina;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.android.insulina.data.InsulinaContract;

public class MainActivity extends AppCompatActivity {

    InsulinaCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        ListView itemsList = (ListView) findViewById(R.id.listViewId);
        // Define a projection
        String[] projection = {
                InsulinaContract.InsulinaEntry._ID,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER
        };

        Cursor cursor = getContentResolver().query(InsulinaContract.InsulinaEntry.CONTENT_URI, projection, null,null, null);

        mCursorAdapter = new InsulinaCursorAdapter(this, cursor);

        itemsList.setAdapter(mCursorAdapter);

        View emptyView = findViewById(R.id.emptyInventoryId);
        itemsList.setEmptyView(emptyView);
    }
}
