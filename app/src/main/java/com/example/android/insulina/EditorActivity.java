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
import android.text.TextUtils;
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

    // Gets user input from editor and saves into the DB
    private void insertEntry() {
        //Reads from user input
        String nameString = mNameEditText.getText().toString().trim();
        String jednostkiString = mJednostkiEditText.getText().toString().trim();
        String opisString = mOpisEditText.getText().toString().trim();
        String cukierString = mCukierEditText.getText().toString().trim();

        // Checks if first 2 entries are provided and saves the data in the DB
        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(jednostkiString)) {
            Toast.makeText(this, R.string.empty_values_toast, Toast.LENGTH_LONG).show();
        } else {
            makeEntry(nameString, jednostkiString, opisString, cukierString);
        }
    }

    // Makes DB Entry or update
    private void makeEntry(String name, String jednostki, String opis, String glukoza) {
        int cukier;
        if("".equals(glukoza)) {
            cukier = 0;
        } else {
            cukier = Integer.parseInt(glukoza);
        }

        int jedn = Integer.parseInt(jednostki);

        // Creates ContentValues object
        ContentValues values = new ContentValues();
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME, name);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE, jedn);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION, opis);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER, cukier);

        // Determine if this is a new entry or not
        if (mCurrentInsulinaUri == null) {
            // This is a new pet
            Uri newUri = getContentResolver().insert(InsulinaContract.InsulinaEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, R.string.toast_entry_not_saved, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.toast_entry_saved, Toast.LENGTH_LONG).show();
            }
        } else {
            // This is an update
            int rowsAffected = getContentResolver().update(mCurrentInsulinaUri, values, null, null);

            // Show a toast message if the update was successful
            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.toast_entry_not_updated, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.toast_entry_updated, Toast.LENGTH_LONG).show();
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
            String laterString;

            /*
            Checks if the user has left the field of Glucose level 2h after eating to null
            If he has then the data in DB is set to "0"
            In order for user to see empty field and not "0" below code set String value to null.
             */
            if(later == 0) {
                laterString = null;
            } else {
                laterString = Integer.toString(later);
            }

            // Update the views on the screen
            mNameEditText.setText(name);
            mJednostkiEditText.setText(Integer.toString(intake));
            mOpisEditText.setText(description);
            mCukierEditText.setText(laterString);
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
