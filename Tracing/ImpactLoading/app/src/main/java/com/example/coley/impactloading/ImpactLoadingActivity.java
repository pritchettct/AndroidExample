package com.example.coley.impactloading;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Author: Coley Pritchett
 * Date created: 6/22/2015
 *
 * This application was created to assist in measuring impact loading
 * by acquiring and displaying raw accelerometer data.
 */
public class ImpactLoadingActivity extends Activity {

    private Button mNewSessionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impact_loading);

        mNewSessionButton = (Button)findViewById(R.id.new_session_button);
        mNewSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ImpactLoadingActivity.this, DataEntryActivity.class);
                startActivity(i);
            }
        });

        /**
         * Check how much memory is available
         */
        StatFs stat = new StatFs(getFilesDir().getAbsolutePath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getBlockCount();
        long megAvailable = bytesAvailable / 1048576;

        /**
         * If there's not much memory, a warning will be shown
         */
        if (megAvailable < 5)
        {
            Toast.makeText(getApplicationContext(), "Caution: not enough memory. Requires 5MB" +
                            "for 1 minute of data.",
                    Toast.LENGTH_LONG).show();
        }
    }

}
