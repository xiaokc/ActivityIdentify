package io.github.zishell.activityidentify;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


import io.github.zishell.utils.FileUtils;
import io.github.zishell.utils.ZLog;

public class SampleDataActivity extends AppCompatActivity {
    private final String TAG = "SampleDataActivity";
    // control
    private boolean isSampling = false;


    private final String FOLDER_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorFiles";
    private String accFileName;
    private String gyroFileName;
    private String linearFileName;

    //view
    private Button btnStart;
    private Button btnEnd;
    private TextView tvShowSampleState;

    //data container
    private HashMap<Long, float[]> accData;
    private HashMap<Long, float[]> gyroData;
    private HashMap<Long, float[]> linearData;

    //sensor
    private SensorManager sensorManager;
    private Sensor accSensor;
    private Sensor linearSensor;
    private Sensor gyroSensor;

    private final int SAVE_ID = 0x1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == SAVE_ID) {
                new AlertDialog.Builder(SampleDataActivity.this).setMessage("save data?")
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                accData = null;
                                gyroData = null;
                                linearData = null;
                            }
                        })
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveSensorData();
                            }
                        })
                        .show();
            }
        }
    };
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event == null || accData == null || gyroData == null || linearData == null)
                return;
            int eventType = event.sensor.getType();
            long ts = System.currentTimeMillis();
            float[] value = event.values.clone();
            switch (eventType) {
                case Sensor.TYPE_ACCELEROMETER:
                    accData.put(ts, value);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyroData.put(ts, value);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    linearData.put(ts, value);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意
                saveSensorData();
            } else {
                ZLog.d(TAG, "用户拒绝授权修改存储卡");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_data);

        initView();
        initSensor();
        setListener();
    }

    private void initView() {
        btnStart = (Button) findViewById(R.id.btn_start);
        btnEnd = (Button) findViewById(R.id.btn_end);
        tvShowSampleState = (TextView) findViewById(R.id.tv_show_sample_state);
        tvShowSampleState.setText("not sampling");
        ZLog.d(TAG, "init views");
    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        ZLog.d(TAG, "init sensors");

    }

    private void setListener() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSampling = true;
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
//                            tvShowSampleState.setText("start sampling");
                            tvShowSampleState.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvShowSampleState.setText("start sampling");
                                }
                            });
                            long ts = System.currentTimeMillis();
                            accFileName = FOLDER_NAME + "/" + ts + ".acc.csv";
                            gyroFileName = FOLDER_NAME + "/" + ts + ".gyro.csv";
                            linearFileName = FOLDER_NAME + "/" + ts + ".linear.csv";
                            registerSensor();
                            Thread.sleep(2500);
                            unregisterSensor();

                            Message msg = new Message();
                            msg.arg1 = SAVE_ID;
                            handler.sendMessage(msg);

                            tvShowSampleState.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvShowSampleState.setText("end sampling");

                                }
                            });
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Thread exception : " + e.toString());
                        }
                    }
                }.start();


            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSampling = false;
            }
        });
    }

    private void registerSensor() {
        accData = new HashMap<>();
        gyroData = new HashMap<>();
        linearData = new HashMap<>();
        sensorManager.registerListener(sensorEventListener, accSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(sensorEventListener, gyroSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(sensorEventListener, linearSensor, SensorManager.SENSOR_DELAY_GAME);
        ZLog.d(TAG, "register sensor listener");
    }

    private void unregisterSensor() {
        sensorManager.unregisterListener(sensorEventListener);
        ZLog.d(TAG, "unregister sensor listener");

    }

    private void saveSensorData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            }

        }


        FileUtils.checkFolderExist(FOLDER_NAME);
        StringBuilder sbacc = new StringBuilder();
        StringBuilder sbgyro = new StringBuilder();
        StringBuilder sblinear = new StringBuilder();
        for (Map.Entry<Long, float[]> entry : accData.entrySet()) {
            long ts = entry.getKey();
            float[] accValue = entry.getValue();
            if (gyroData.containsKey(ts)) {
                float[] gyroValue = gyroData.get(ts);
                sbacc.append("acc," + ts + "," + accValue[0] + "," + accValue[1] + "," + accValue[2] + "\n");
                sbgyro.append("gyro," + ts + "," + gyroValue[0] + "," + gyroValue[1] + "," + gyroValue[2] + "\n");
            }
        }
        for (Map.Entry<Long, float[]> entry : linearData.entrySet()) {
            long ts = entry.getKey();
            float[] linearValue = entry.getValue();
            sblinear.append("linear," + ts + "," + linearValue[0] + "," + linearValue[1] + "," + linearValue[2] + "\n");
        }

        FileUtils.writeStringToFile(accFileName, sbacc.toString());
        ZLog.d(TAG, "saved acc sensor data to " + accFileName);

        FileUtils.writeStringToFile(gyroFileName, sbgyro.toString());
        ZLog.d(TAG, "saved gyro sensor data " + gyroFileName);

        FileUtils.writeStringToFile(linearFileName, sblinear.toString());
        ZLog.d(TAG, "saved linear sensor data " + linearFileName);

        accData = null;
        gyroData = null;
        linearData = null;
    }
}
