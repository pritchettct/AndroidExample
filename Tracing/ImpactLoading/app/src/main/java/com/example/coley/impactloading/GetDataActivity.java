package com.example.coley.impactloading;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * GetDataActivity displays the results of the accelerometer data collection
 * as well as the settings entered in DataEntryActivity.
 */
public class GetDataActivity extends Activity {

    private TextView dataText;
    private String sessionTitle;
    private String deviceLocation;
    private String trial;
    private int currentapiVersion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);

        currentapiVersion = android.os.Build.VERSION.SDK_INT;

        /**
         * Gets settings from DataEntryActivity
         */
        SharedPreferences saved_values =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sessionTitle = saved_values.getString("title", "Can't find file.");
        deviceLocation = saved_values.getString("location", "Can't find the file.");
        trial = saved_values.getString("trial", "Can't find the file.");

        dataText = (TextView) findViewById(R.id.data_text);
        dataText.setText(readFromFile());

        /**
         * Ensures app will work with phones with older APIs
         */
        if (currentapiVersion < 11) {
            registerForContextMenu(dataText);
        }
        else {
            dataText.setTextIsSelectable(true);
        }

    }

    /**
     * For copying data
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        TextView textView = (TextView) view;
        menu.setHeaderTitle(textView.getText()).add(0, 0, 0, "Copy to clipboard.");
    }

    /**
     * For copying data
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setText(dataText.getText());
        return true;
    }

    /**
     * Displays accelerometer data by reading internal txt file along with preferences set
     * in DataEntryActivity
     */
    private String readFromFile() {
        String ret = "";

        try {
            InputStream inputStream = openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Session: " + sessionTitle + "\n");
                stringBuilder.append("Device Location: " + deviceLocation + "\n");
                stringBuilder.append("Trial: " + trial + "\n");
                stringBuilder.append("X, Y, Z, Time Stamp\n");

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Cannot read file: " + e.toString());
        }

        return ret;
    }
}
