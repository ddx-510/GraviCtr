package com.ddx.hacknroll;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor mSensor;

    /*
    DEBUG
    private TextView textviewX;
    private TextView textviewY;
    private TextView textviewZ;
    */
    

    private boolean flagX = true;           // For first recording of X,Z values
    private boolean flagZ = true;


    private int mX, mY, mZ;
    private long lasttimestamp = 0;
    Calendar mCalendar;


    public MainActivity() {
        mCalendar = Calendar.getInstance();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        // Go to setting directly for accessibility open

        /*
        DEBUGtextviewX = findViewById(R.id.textView1);
        textviewY = findViewById(R.id.textView3);
        textviewZ = findViewById(R.id.textView4);
        */

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
        if (null == mSensorManager) {
            Log.d(TAG, "deveice not support SensorManager");
        }
        //
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);// S

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mCalendar = Calendar.getInstance();
        long stamp = mCalendar.getTimeInMillis() / 1000l;// 1393844912
        if (event.sensor == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];

            /*DEBUG
            textviewX.setText(String.valueOf(x));
            textviewY.setText(String.valueOf(y));
            textviewZ.setText(String.valueOf(z));
            */

            int second = mCalendar.get(Calendar.SECOND);

            // find change
            int px = Math.abs(mX - x);
            int py = Math.abs(mY - y);
            int pz = Math.abs(mZ - z);

            /* log debug
            //Log.d(TAG, "pX:" + px + "  pY:" + py + "  pZ:" + pz + "    stamp:"
                    //+ stamp + "  second:" + second + "  lasttimestamp:" + lasttimestamp);
            */

            //int maxvalue = getMaxValue(px, py, pz);

            if (flagX == true) { // for first time detection of x-axis change
                if (px > 4 && (stamp - lasttimestamp) > 0) {   // detect a instant increase in x-axis
                    lasttimestamp = stamp;
                    //Log.d(TAG, "First Should return");
                    flagX = false;
                    back();
                }

            } else {
                if (px > 4 && (stamp - lasttimestamp) > 1) {   // detect a instant increase in x-axis
                    lasttimestamp = stamp;
                    //Log.d(TAG, "Should return");
                    back();
                }
            }

            if (flagZ == true) { // for first time detection of z-axis change
                if (pz > 4 && (stamp - lasttimestamp) > 0) {   // detect a instant increase in z-axis
                    lasttimestamp = stamp;
                    //Log.d(TAG, "First Should Home");
                    // home
                    home();
                    flagZ = false;
                }

            } else {
                if (pz > 4 && (stamp - lasttimestamp) > 1) {   // detect a instant increase in z-axis
                    lasttimestamp = stamp;
                    //Log.d(TAG, "Should home");
                    home();
                }
            }

            mX = x;
            mY = y;
            mZ = z;
        }
    }



    // send home
    public void home(){
        EventBus.getDefault().post(MyAccessibilityService.HOME);
    }

    //send back
    public void back(){
        EventBus.getDefault().post(MyAccessibilityService.BACK);
    }

}

