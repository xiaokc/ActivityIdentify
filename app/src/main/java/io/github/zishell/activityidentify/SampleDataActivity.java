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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.github.zishell.activityidentify.model.Gesture;
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

        String g1f = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorFiles/1478403428684.linear.csv";
        String g2f = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorFiles/1478403438418.linear.csv";
        String g3f = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorFiles/1478403448474.linear.csv";
        String g4f = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorFiles/1478404617102.linear.csv";

        Gesture g1 = new Gesture();
        g1.initFromCSVFile(g1f);
        g1.normalization();
        g1.cutOff();
        Gesture g2 = new Gesture();
        g2.initFromCSVFile(g2f);
        g2.normalization();
        g2.cutOff();
        Gesture g3 = new Gesture();
        g3.initFromCSVFile(g3f);
        g3.normalization();
        g3.cutOff();

        Gesture g4= new Gesture();
        g4.initFromCSVFile(g4f);
        g4.normalization();
        g4.cutOff();

        float d11Base = DTW.getBaseGestureDistance(g1, g1);
        float d12Base = DTW.getBaseGestureDistance(g1, g2);
        float d13Base = DTW.getBaseGestureDistance(g1, g3);
        float d23Base = DTW.getBaseGestureDistance(g2, g3);
        float d32Base = DTW.getBaseGestureDistance(g3, g2);
        float d14Base = DTW.getBaseGestureDistance(g1, g4);
        float d24Base = DTW.getBaseGestureDistance(g2, g4);
        float d34Base = DTW.getBaseGestureDistance(g3, g4);

        System.out.println("==>Gesture Base d11: " + d11Base);
        System.out.println("==>Gesture Base d12: " + d12Base);
        System.out.println("==>Gesture Base d13: " + d13Base);
        System.out.println("==>Gesture Base d23: " + d23Base);
        System.out.println("==>Gesture Base d32: " + d32Base);
        System.out.println("==>Gesture Base d14: " + d14Base);
        System.out.println("==>Gesture Base d24: " + d24Base);
        System.out.println("==>Gesture Base d34: " + d34Base);

        float d11 = DTW.getGestureDistance(g1, g1);
        float d12 = DTW.getGestureDistance(g1, g2);
        float d13 = DTW.getGestureDistance(g1, g3);
        float d23 = DTW.getGestureDistance(g2, g3);
        float d32 = DTW.getGestureDistance(g3, g2);
        float d14 = DTW.getGestureDistance(g1, g4);
        float d24 = DTW.getGestureDistance(g2, g4);
        float d34 = DTW.getGestureDistance(g3, g4);

        System.out.println("==>Gesture d11: " + d11);
        System.out.println("==>Gesture d12: " + d12);
        System.out.println("==>Gesture d13: " + d13);
        System.out.println("==>Gesture d23: " + d23);
        System.out.println("==>Gesture d32: " + d32);
        System.out.println("==>Gesture d14: " + d14);
        System.out.println("==>Gesture d24: " + d24);
        System.out.println("==>Gesture d34: " + d34);
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
//                            Thread.sleep(2500);

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

        //asc sort
        List<Map.Entry<Long, float[]>> listAccData = new ArrayList<>(accData.entrySet());
        Collections.sort(listAccData, new Comparator<Map.Entry<Long, float[]>>() {
                    @Override
                    public int compare(Map.Entry<Long, float[]> o1, Map.Entry<Long, float[]> o2) {
                        return (int) (o1.getKey() - o2.getKey());
                    }
                }
        );
        List<Map.Entry<Long, float[]>> listLinearData = new ArrayList<>(linearData.entrySet());
        Collections.sort(listAccData, new Comparator<Map.Entry<Long, float[]>>() {
                    @Override
                    public int compare(Map.Entry<Long, float[]> o1, Map.Entry<Long, float[]> o2) {
                        return (int) (o1.getKey() - o2.getKey());
                    }
                }
        );
        ArrayList<Float> lx = new ArrayList<>();
        ArrayList<Float> ly = new ArrayList<>();
        ArrayList<Float> lz = new ArrayList<>();
        ArrayList<Float> ax = new ArrayList<>();
        ArrayList<Float> ay = new ArrayList<>();
        ArrayList<Float> az = new ArrayList<>();
        ArrayList<Float> gx = new ArrayList<>();
        ArrayList<Float> gy = new ArrayList<>();
        ArrayList<Float> gz = new ArrayList<>();

        for (int i = 0; i < listAccData.size(); i++) {
            long ts = listAccData.get(i).getKey();
            float[] accValue = listAccData.get(i).getValue();
            if (gyroData.containsKey(ts)) {
                float[] gyroValue = gyroData.get(ts);
                ax.add(accValue[0]);
                ay.add(accValue[1]);
                az.add(accValue[2]);
                gx.add(gyroValue[0]);
                gy.add(gyroValue[1]);
                gz.add(gyroValue[2]);
//                sbacc.append("acc," + ts + "," + accValue[0] + "," + accValue[1] + "," + accValue[2] + "\n");
//                sbgyro.append("gyro," + ts + "," + gyroValue[0] + "," + gyroValue[1] + "," + gyroValue[2] + "\n");
            }
        }
        for (int i = 0; i < listLinearData.size(); i++) {
            long ts = listLinearData.get(i).getKey();
            float[] linearValue = listLinearData.get(i).getValue();
            lx.add(linearValue[0]);
            ly.add(linearValue[1]);
            lz.add(linearValue[2]);
//            sblinear.append("linear," + ts + "," + linearValue[0] + "," + linearValue[1] + "," + linearValue[2] + "\n");
        }

//        FileUtils.writeStringToFile(accFileName, sbacc.toString());
//        ZLog.d(TAG, "saved acc sensor data to " + accFileName);

//        FileUtils.writeStringToFile(gyroFileName, sbgyro.toString());
//        ZLog.d(TAG, "saved gyro sensor data " + gyroFileName);

//        FileUtils.writeStringToFile(linearFileName, sblinear.toString());
//        ZLog.d(TAG, "saved linear sensor data " + linearFileName);

        Gesture gestrue1 = new Gesture(lx, ly, lz, ax, ay, az, gx, gy, gz);
        ZLog.d(TAG, gestrue1.toString());
        gestrue1.saveToCSV(linearFileName);


        accData = null;
        gyroData = null;
        linearData = null;
    }
}
