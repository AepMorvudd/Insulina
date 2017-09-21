package com.example.android.insulina;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.insulina.data.InsulinaContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText mNameEditText, mJednostkiEditText, mOpisEditText, mCukierEditText, mJednostkiNaZbicieEditText, mCukierPrzedEditText;

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
        if (mCurrentInsulinaUri == null) {
            setTitle(R.string.add_item_heading);
            visibilityGone();
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_item_heading);

            // Initialise a loader
            getLoaderManager().initLoader(EXISTING_ENTRY_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.item_name_edit_text);
        mJednostkiEditText = (EditText) findViewById(R.id.item_jednostki_edit_text);
        mOpisEditText = (EditText) findViewById(R.id.item_description_edit_text);
        mCukierEditText = (EditText) findViewById(R.id.item_glucose_edit_text);
        mJednostkiNaZbicieEditText = (EditText) findViewById(R.id.intake_to_get_down);
        mCukierPrzedEditText = (EditText) findViewById(R.id.item_glucose_before);

        // Hides keyboard when activity starts
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Sets keyboard to numerical
        mJednostkiEditText.setRawInputType(Configuration.KEYBOARD_12KEY);
        mCukierEditText.setRawInputType(Configuration.KEYBOARD_QWERTY);
        mJednostkiNaZbicieEditText.setRawInputType(Configuration.KEYBOARD_12KEY);
        mCukierPrzedEditText.setRawInputType(Configuration.KEYBOARD_QWERTY);
    }

    // Sets visibility of items description to gone
    private void visibilityGone() {
        TextView nameView, intakeView, descView, laterView, intakeBeforeView, glucoseBeforeView;
        nameView = (TextView) findViewById(R.id.object_product_name);
        nameView.setVisibility(View.GONE);
        intakeView = (TextView) findViewById(R.id.object_intake);
        intakeView.setVisibility(View.GONE);
        descView = (TextView) findViewById(R.id.object_desc);
        descView.setVisibility(View.GONE);
        laterView = (TextView) findViewById(R.id.object_glucose_later);
        laterView.setVisibility(View.GONE);
        intakeBeforeView = (TextView) findViewById(R.id.object_intake_to_down);
        intakeBeforeView.setVisibility(View.GONE);
        glucoseBeforeView = (TextView) findViewById(R.id.object_glucose_before);
        glucoseBeforeView.setVisibility(View.GONE);
    }

    // Gets user input from editor and saves into the DB
    private void insertEntry() {
        //Reads from user input
        String nameString = mNameEditText.getText().toString().trim();
        String jednostkiString = mJednostkiEditText.getText().toString().trim();
        String opisString = mOpisEditText.getText().toString().trim();
        String cukierString = mCukierEditText.getText().toString().trim();
        String jednostkiNaZbicieString = mJednostkiNaZbicieEditText.getText().toString().trim();
        String cukierPrzedString = mCukierPrzedEditText.getText().toString().trim();

        // Checks if used set double values with ","
        // If he did changes it to "."
        if (jednostkiString.contains(",")) {
            String parts[] = jednostkiString.split("\\,");
            jednostkiString = parts[0] + "." + parts[1];
        }
        if (jednostkiNaZbicieString.contains(",")) {
            String parts[] = jednostkiNaZbicieString.split("\\,");
            jednostkiNaZbicieString = parts[0] + "." + parts[1];
        }

        // Checks if first 2 entries are provided and saves the data in the DB
        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(jednostkiString)) {
            Toast.makeText(this, R.string.empty_values_toast, Toast.LENGTH_LONG).show();
        } else {
            makeEntry(nameString, jednostkiString, opisString, cukierString, jednostkiNaZbicieString, cukierPrzedString);
        }
    }

    // Makes DB Entry or update
    private void makeEntry(String name, String jednostki, String opis, String glukoza, String jednostkiNaZbicie, String glukozaPrzed) {
        int cukier;
        if ("".equals(glukoza)) {
            cukier = 0;
        } else {
            cukier = Integer.parseInt(glukoza);
        }
        double jednostkiNaObniżenie;
        if ("".equals(jednostkiNaZbicie)) {
            jednostkiNaObniżenie = 0.0;
        } else {
            jednostkiNaObniżenie = Double.parseDouble(jednostkiNaZbicie);
        }
        int cukierPrzed;
        if ("".equals(glukozaPrzed)) {
            cukierPrzed = 0;
        } else {
            cukierPrzed = Integer.parseInt(glukozaPrzed);
        }

        double jedn = Double.parseDouble(jednostki);

        // Creates ContentValues object
        ContentValues values = new ContentValues();
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME, name);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE, jedn);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION, opis);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER, cukier);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_INTAKE_TO_DOWN, jednostkiNaObniżenie);
        values.put(InsulinaContract.InsulinaEntry.COLUMN_GLUCOSE_BEFORE, cukierPrzed);

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new entry, hide the "Detele" menu item.
        if (mCurrentInsulinaUri == null) {
            MenuItem menuItem = menu.findItem(R.id.menu_delete);
            menuItem.setVisible(false);
        }
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
            //Respond to delete button click
            case R.id.menu_delete:
                showDeleteConfirmationDialog();
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
                InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER,
                InsulinaContract.InsulinaEntry.COLUMN_GLUCOSE_BEFORE,
                InsulinaContract.InsulinaEntry.COLUMN_INTAKE_TO_DOWN
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
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find teh columns that we are interested in
            int nameColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_NAME);
            int intakeColumnIndes = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_INTAKE);
            int descColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_DESCRIPTION);
            int laterColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INSULINA_GLUCOSE_2H_LATER);
            int insulinaPrzedColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_INTAKE_TO_DOWN);
            int cukierPrzedColumnIndex = cursor.getColumnIndex(InsulinaContract.InsulinaEntry.COLUMN_GLUCOSE_BEFORE);

            // Extract out the values for the given column index
            String name = cursor.getString(nameColumnIndex);
            double intake = cursor.getDouble(intakeColumnIndes);
            String description = cursor.getString(descColumnIndex);
            int later = cursor.getInt(laterColumnIndex);
            double insulinaPrzed = cursor.getDouble(insulinaPrzedColumnIndex);
            int cukierPrzed = cursor.getInt(cukierPrzedColumnIndex);

            String laterString, intakePrior, glucosePrior;
            /*
            Checks if the user has left the field of Glucose level 2h after eating, Intake Prior Eating and / or Glucose Prior Eating to null
            If he has then the data in DB is set to "0"
            In order for user to see empty field and not "0" below code set String value to null.
             */
            if (later == 0) {
                laterString = null;
            } else {
                laterString = Integer.toString(later);
            }
            if (insulinaPrzed == 0.0) {
                intakePrior = null;
            } else {
                intakePrior = Double.toString(insulinaPrzed);
            }
            if (cukierPrzed == 0) {
                glucosePrior = null;
            } else {
                glucosePrior = Integer.toString(cukierPrzed);
            }

            // Update the views on the screen
            mNameEditText.setText(name);
            mJednostkiEditText.setText(Double.toString(intake));
            mOpisEditText.setText(description);
            mCukierEditText.setText(laterString);
            mJednostkiNaZbicieEditText.setText(intakePrior);
            mCukierPrzedEditText.setText(glucosePrior);
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

    // Show a dialog for user to confirm if he really wants to delete an entry
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);

        // Code for when user wants to delete
        builder.setPositiveButton(R.string.delete_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEntry();
            }
        });
        // Code for when user wants to abort
        builder.setNegativeButton(R.string.delete_deny, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Creates and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Delete entry
    private void deleteEntry() {
        // Check if this is an existing entry
        if (mCurrentInsulinaUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInsulinaUri, null, null);

            // Show a toast if entry deleted or not
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.toast_entry_not_deleted, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.toast_entry_deleted, Toast.LENGTH_LONG).show();
            }
        }

        // Close the activity
        finish();
    }
}
