package com.example.android.insulina;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.insulina.data.InsulinaContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    EditText mNameEditText, mJednostkiEditText, mOpisEditText, mCukierEditText;

    private Uri mCurrentInsulinaUri;

    private static final int EXISTING_ENTRY_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_information);

        // sets Proper heading for EditorsActivitity
        // Wether it's Editing or Entering a enw pet
        Intent intent = getIntent();
        mCurrentInsulinaUri = intent.getData();
        if(mCurrentInsulinaUri == null) {
            setTitle(R.string.add_item_heading);
        } else {
            setTitle(R.string.edit_item_heading);

            // Initialise a loader
            getLoaderManager().initLoader(EXISTING_ENTRY_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.item_name_edit_text);
        mJednostkiEditText = (EditText) findViewById(R.id.item_jednostki_edit_text);
        mOpisEditText = (EditText) findViewById(R.id.item_description_edit_text);
        mCukierEditText = (EditText) findViewById(R.id.item_glucose_edit_text);
    }

    /*
    Gets user input from editor and saves into the DB
     */
    private void insertEntry() {
        //Reads from user input
        String nameString = mNameEditText.getText().toString().trim();
        String jednostkiString = mJednostkiEditText.getText().toString().trim();
        String opisString = mOpisEditText.getText().toString().trim();
        String cukierString = mCukierEditText.getText().toString().trim();
        int jednostki = Integer.parseInt(jednostkiString);
        int cukier = Integer.parseInt(cukierString);

        // Creates ContentValues object
        ContentValues values = new ContentValues();
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME, nameString);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE, jednostki);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION, opisString);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER, cukier);

        // Determine if this is a new entry or not
        if(mCurrentInsulinaUri == null) {
            // This is a new pet
            Uri newUri = getContentResolver().insert(InsulinaContract.InsulinaEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error with saving entry", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Entry saved.", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.v("EditorActivity", "values size is: " + values.size());
            Log.v("EditorActivity", "values are: " + values);
            // This is an update
            int rowsAffected = getContentResolver().update(mCurrentInsulinaUri, values, null, null);

            // Show a toast message if the update was successful
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with updating entry", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Entry updated", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a save button click
            case R.id.menu_save:
                // Save entry to DB
                insertEntry();
                // Exit activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection
        String[] projection = {
                InsulinaContract.InsulinaEntry._ID,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION,
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER
        };

        return new CursorLoader(this,
                mCurrentInsulinaUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cusros
        if (cursor == null || cursor .getCount() <1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if(cursor.moveToFirst()) {
            // Find teh columns that we are interested in
            int nameColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME);
            int intakeColumnIndes = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE);
            int descColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION);
            int laterColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER);

            // Extract out the values for the given column index
            String name = cursor.getString(nameColumnIndex);
            int intake = cursor.getInt(intakeColumnIndes);
            String description = cursor.getString(descColumnIndex);
            int later = cursor.getInt(laterColumnIndex);

            // Update the views on the screen
            mNameEditText.setText(name);
            mJednostkiEditText.setText(Integer.toString(intake));
            mOpisEditText.setText(description);
            mCukierEditText.setText(Integer.toString(later));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mJednostkiEditText.setText("");
        mOpisEditText.setText("");
        mCukierEditText.setText("");
    }
}
