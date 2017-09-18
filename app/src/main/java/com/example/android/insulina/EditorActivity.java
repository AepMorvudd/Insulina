package com.example.android.insulina;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.insulina.data.InsulinaContract;

public class EditorActivity extends AppCompatActivity{

    EditText mNameEditText, mJednostkiEditText, mOpisEditText, mCukierEditText;

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

        Uri newUri = getContentResolver().insert(InsulinaContract.InsulinaEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, "Error with saving entry", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Entry saved.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_information);

        // sets Proper heading for EditorsActivitity
        // Wether it's Editing or Entering a enw pet
        Intent intent = getIntent();
        Uri currentInsulinaUri = intent.getData();
        if(currentInsulinaUri == null) {
            setTitle(R.string.add_item_heading);
        } else {
            setTitle(R.string.edit_item_heading);
        }

        mNameEditText = (EditText) findViewById(R.id.item_name_edit_text);
        mJednostkiEditText = (EditText) findViewById(R.id.item_jednostki_edit_text);
        mOpisEditText = (EditText) findViewById(R.id.item_description_edit_text);
        mCukierEditText = (EditText) findViewById(R.id.item_glucose_edit_text);
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
}
