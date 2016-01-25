package com.example.coley.impactloading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Allows user to enter settings that will be displayed.
 */
public class DataEntryActivity extends Activity {
    public static final String PREFS = "PrefsFile";

    private Button mBeginSessionButton;
    private EditText sessionTitle;
    private Spinner locationSpinner;
    private Spinner trialSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        sessionTitle = (EditText) findViewById(R.id.session_title);
        locationSpinner = (Spinner) findViewById(R.id.device_location_spinner);
        trialSpinner = (Spinner) findViewById(R.id.trials_spinner);

        /**
         * When a new session is started, the choices are saved so that they can be
         * displayed with the rest of the data in GetDataActivity.
         */
        mBeginSessionButton = (Button)findViewById(R.id.begin_session_button);
        mBeginSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = saved_values.edit();
                editor.putString("title", sessionTitle.getText().toString());
                editor.putString("trial", String.valueOf(trialSpinner.getSelectedItem()));
                editor.putString("location", String.valueOf(locationSpinner.getSelectedItem()));
                editor.apply();
                Intent intent = new Intent(DataEntryActivity.this, SessionActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Name of the activity is set to the date by default
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateAndTime = sdf.format(new Date());
        sessionTitle.setText(currentDateAndTime);
    }
}
