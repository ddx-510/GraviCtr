package com.ddx.hacknroll;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView textviewX;
    private TextView textviewY;
    private TextView textviewZ;
    

    private boolean flagX = true;
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
        textviewX = (TextView) findViewById(R.id.textView1);
        textviewY = (TextView) findViewById(R.id.textView3);
        textviewZ = (TextView) findViewById(R.id.textView4);

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

            textviewX.setText(String.valueOf(x));
            textviewY.setText(String.valueOf(y));
            textviewZ.setText(String.valueOf(z));

            int second = mCalendar.get(Calendar.SECOND);

            // find change
            int px = Math.abs(mX - x);
            int py = Math.abs(mY - y);
            int pz = Math.abs(mZ - z);

            // log debug
            Log.d(TAG, "pX:" + px + "  pY:" + py + "  pZ:" + pz + "    stamp:"
                    + stamp + "  second:" + second + "  lasttimestamp:" + lasttimestamp);

            //int maxvalue = getMaxValue(px, py, pz);

            if (flagX == true) { // for first time detection of x-axis change
                if (px > 4 && (stamp - lasttimestamp) > 0) {   // detect a instant increase in x-axis
                    lasttimestamp = stamp;
                    Log.d(TAG, "First Should return");
                    flagX = false;
                    back();
                }

            } else {
                if (px > 4 && (stamp - lasttimestamp) > 1) {   // detect a instant increase in x-axis
                    lasttimestamp = stamp;
                    Log.d(TAG, "Should return");
                    back();
                }
            }

            if (flagZ == true) { // for first time detection of z-axis change
                if (pz > 4 && (stamp - lasttimestamp) > 0) {   // detect a instant increase in z-axis
                    lasttimestamp = stamp;
                    Log.d(TAG, "First Should Home");
                    // home
                    home();
                    flagZ = false;
                }

            } else {
                if (pz > 4 && (stamp - lasttimestamp) > 1) {   // detect a instant increase in z-axis
                    lasttimestamp = stamp;
                    Log.d(TAG, "Should home");
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





   /** FIND MAX BTW X,Y,Z
    public int getMaxValue(int px, int py, int pz) {
        int max = 0;
        if (px > py && px > pz) {
            max = px;
        } else if (py > px && py > pz) {
            max = py;
        } else if (pz > px && pz > py) {
            max = pz;
        }

        return max;
    }
    **/

            /** if (event.sensor == null) {
             return;
             }

             if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
             x_a = (int) event.values[0];
             y_a = (int) event.values[1];
             z_a = (int) event.values[2];
             first3 = true;
             }
             if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
             x_g = (int) event.values[0];
             y_g = (int) event.values[1];
             z_g = (int) event.values[2];
             secon3 = true;
             }

             if (first3 && secon3) {
             if (count == 32) {
             for (int i = 0; i < 6; i++) {
             recordArray.remove(0);
             }
             count -= 1;
             }
             first3 = false;
             secon3 = false;


             recordArray.add(x_a);
             recordArray.add(y_a);
             recordArray.add(z_a);
             recordArray.add(x_g);
             recordArray.add(y_g);
             recordArray.add(z_g);
             count += 1;

             threshold = false;

             for (int i = 0; i < count * 6; i++) {

             recordingData[i] = recordArray.get(i);

             if (i % 6 == 0 && (Math.abs(recordingData[i]) >= THRESHOLD_VALUE || Math.abs(recordingData[i+1]) >= THRESHOLD_VALUE ||  Math.abs(recordingData[i+2]) >= THRESHOLD_VALUE)) {

             int now = mCalendar.get(Calendar.MILLISECOND);
             Log.d("time",Integer.toString(now));
             Log.d("time",Integer.toString(prev_second));
             if (prev_second == 0 || (now - prev_second > 640)) {
             threshold = true;
             Log.d("time", Integer.toString(prev_second) + ' ' + Integer.toString(mCalendar.get(Calendar.SECOND)));
             prev_second = now;
             }
             }
             }
             }

             Log.d("hi","hi");
             if (count == 32 && threshold) {

             inferenceInterface.feed(INPUT_NODE, recordingData, INPUT_SIZE);
             inferenceInterface.run(OUTPUT_NODES);
             inferenceInterface.fetch(OUTPUT_NODE, outputScores);

             maxi = outputScores[0];

             Log.d("Action", Long.toString(maxi));
             if (2 == maxi){
             home();
             }
             else if (1 == maxi){
             back();
             }
             else if (0 == maxi){
             menu();
             }
             else if (3 == maxi){
             notice();
             }
             }




             // start service for control
             startService(new Intent(this,MyAccessibilityService.class));
             super.onCreate(savedInstanceState);
             setContentView(R.layout.activity_main);

             // init value on screen
             textviewX = findViewById(R.id.textView1);
             textviewY = findViewById(R.id.textView3);
             textviewZ = findViewById(R.id.textView4);

             mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
             mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);  // acceleration
             gSensor =  mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

             if (null == mSensorManager) {
             Log.d(TAG, "device not support SensorManager");
             }

             mSensorManager.registerListener(this, gSensor,delay);
             mSensorManager.registerListener(this, mSensor,delay);

             loadTensorflow(); // where should i put this?????????????????????????????????????????????????

             **/