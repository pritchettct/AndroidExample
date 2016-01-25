package com.example.coley.impactloading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This activity uses the accelerometer to record the data.
 */
public class SessionActivity extends Activity implements SensorEventListener {
    private Chronometer mChronometer;
    private Button mStartButton, mStopButton, mDataButton, mClearButton;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    RandomAccessFile raf;

    private TextView currentX, currentY, currentZ;
    public float x = 0;
    public float y = 0;
    public float z = 0;
    private SimpleDateFormat sdf;
    private String currentTime;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        /**
         * prevent screen from locking (which turns off the accelerometer)
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /**
         * sdf for time stamp
         */
        sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        File file = new File(this.getFilesDir(), "config.txt");

        try {
            raf = new RandomAccessFile(file, "rw");
        } catch (IOException ioe) {
            System.out.print("IOException");
        }

        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        mChronometer = (Chronometer)findViewById(R.id.chronometer);

        mStartButton = (Button)findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    raf.setLength(0);
                } catch (IOException ioe) {
                    System.out.print("IOException");
                }
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.start();
                onResume();
            }
        });
        mStopButton = (Button)findViewById(R.id.stop_button);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChronometer.stop();
                onPause();
            }
        });

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mDataButton = (Button)findViewById(R.id.data_button);
        mDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                Intent i = new Intent(SessionActivity.this, GetDataActivity.class);
                startActivity(i);
            }
        });

        mClearButton = (Button)findViewById(R.id.clear_button);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    raf.setLength(0);
                } catch (IOException ioe) {
                    System.out.print("IOException");
                }
            }
        });

    } // end of onCreate


    /**
     * register the accelerometer for listening the events
     */
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * unregister the accelerometer for stop listening the events
     */
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        /**
         * Creates a string of accelerometer data and time and writes it to file
         */
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];
            currentTime = sdf.format(new Date());
            data = x + ", " + y + ", " + z + ", " + currentTime + "\n";
            writeToFile(data);
        }
        displayCurrentValues();
    }

    /**
     * display the current x,y,z accelerometer values
     */
    public void displayCurrentValues() {
        /**
         * DecimalFormat to prevent multiline values on smaller screens
         */
        DecimalFormat df = new DecimalFormat("##.#####");
        String newX = df.format(x);
        String newY = df.format(y);
        String newZ = df.format(z);
        currentX.setText(newX);
        currentY.setText(newY);
        currentZ.setText(newZ);
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config.txt", Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
