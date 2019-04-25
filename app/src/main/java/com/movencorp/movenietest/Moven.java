package com.movencorp.movenietest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class Moven extends AppCompatActivity {
    private static final String LOG_TAG = "MovenReader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTest(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doTest(View view) {
        Uri uri = new Uri.Builder().scheme("content").authority("com.moven.cordova.sharedsecret.ietest.SharedSecretProvider").appendPath("MovenWellnessSettings").build();
        ContentResolver cr = getContentResolver();
        String value = getValue(cr, uri, "MovenToken");
        Snackbar.make(view, value == null ? "(null)" : value, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        if (value != null) {
            updateValue(cr, uri, "MovenToken", "", value);
        }

    }

    private String getValue(ContentResolver cr, Uri uri, String key)
    {
        Log.d(LOG_TAG, "Looking for '" + key + "'");
        Cursor theCursor = cr.query(uri, new String[] { "value" }, "key = ?", new String[] { key },null);
        if (theCursor == null) {
            Log.d(LOG_TAG, "Error looking for find key '" + key + "'");
            return null;
        }

        if (theCursor.getCount() < 1) {
            Log.d(LOG_TAG, "No key ''" + key + "'");
            theCursor.close();
            return null;
        }

        theCursor.moveToNext();

        String value = theCursor.getString(0);
        theCursor.close();

        return value;
    }

    private void updateValue(ContentResolver cr, Uri uri, String key, String value, String current)
    {
        if (current == null) {
            if (value != null && value.length() > 0)
            {
                // insert
                Log.d(LOG_TAG, "Inserting key '" + key + "'");
                ContentValues cv = new ContentValues();
                cv.put("key", key);
                cv.put("value", value);
                cr.insert(uri, cv);
            }
            else
            {
                Log.d(LOG_TAG, "No need to delete key '" + key + "'");
            }
        }
        else {
            if (value == null || value.length() == 0)
            {
                // delete
                Log.d(LOG_TAG, "Deleting key '" + key + "'");
                cr.delete(uri, "key = ?", new String[] { key });
            }
            else if (current.equals(value))
            {
                // do nothing
                Log.d(LOG_TAG, "No update required for key '" + key + "'");
            }
            else {
                // update
                Log.d(LOG_TAG, "Updating key '" + key + "'");
                ContentValues cv = new ContentValues();
                cv.put("key", key);
                cv.put("value", value);
                cr.update(uri, cv, null, null);
            }
        }
    }

}
