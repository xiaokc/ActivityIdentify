package io.github.zishell.activityidentify.model;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import io.github.zishell.utils.FileUtils;
import io.github.zishell.utils.ZLog;


/**
 * Created by zishe on 2016/11/5.
 */

public class Gesture {
    private final String TAG = "Gesture";
    private String user;
    private String gestureName;
    private ArrayList<Float> lx;
    private ArrayList<Float> ly;
    private ArrayList<Float> lz;
    private ArrayList<Float> ax;
    private ArrayList<Float> ay;
    private ArrayList<Float> az;
    private ArrayList<Float> gx;
    private ArrayList<Float> gy;
    private ArrayList<Float> gz;

    public Gesture() {
        this.lx = new ArrayList<>();
        this.ly = new ArrayList<>();
        this.lz = new ArrayList<>();
        this.ax = new ArrayList<>();
        this.ay = new ArrayList<>();
        this.az = new ArrayList<>();
        this.gx = new ArrayList<>();
        this.gy = new ArrayList<>();
        this.gz = new ArrayList<>();
        this.user = "unknown";
        this.gestureName = "unknown";
    }

    public void initFromCSVFile(String fileName) {
        String encoding = "UTF-8";
        try {
            File file = new File(fileName);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                bufferedReader.readLine();//pass first line
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] list = line.split(",");
                    this.user = list[0];
                    this.gestureName = list[1];
                    if (list[2].length() > 0) {
                        this.lx.add(Float.valueOf(list[2]));
                        this.ly.add(Float.valueOf(list[3]));
                        this.lz.add(Float.valueOf(list[4]));
                    }
                    if (list.length > 5) {
                        this.ax.add(Float.valueOf(list[5]));
                        this.ay.add(Float.valueOf(list[6]));
                        this.az.add(Float.valueOf(list[7]));
                        this.gx.add(Float.valueOf(list[8]));
                        this.gy.add(Float.valueOf(list[9]));
                        this.gz.add(Float.valueOf(list[10]));
                    }
                }
                read.close();
            } else {
                ZLog.e(TAG, "Can't find file: " + fileName);
            }
        } catch (Exception e) {
            ZLog.e(TAG, "Exception when read file: " + fileName + "\n" + e.toString());
        }
    }

    public Gesture(ArrayList<Float> lx, ArrayList<Float> ly, ArrayList<Float> lz) {
        this.lx = lx;
        this.ly = ly;
        this.lz = lz;
        this.ax = null;
        this.ay = null;
        this.az = null;
        this.gx = null;
        this.gy = null;
        this.gz = null;
        this.user = "unknown";
        this.gestureName = "unknown";

    }

    public Gesture(ArrayList<Float> lx, ArrayList<Float> ly, ArrayList<Float> lz, String user, String gName) {
        this.lx = lx;
        this.ly = ly;
        this.lz = lz;
        this.ax = null;
        this.ay = null;
        this.az = null;
        this.gx = null;
        this.gy = null;
        this.gz = null;
        this.user = user;
        this.gestureName = gName;

    }

    public Gesture(float[] lx, float[] ly, float[] lz, String user, String gName) {
        boolean flag = (lx.length == ly.length) && (ly.length == lz.length);
        assert flag : "the lenght of lx ly lz must be the same.";
        for (int i = 0; i < lx.length; i++) {
            this.lx.add(lx[i]);
            this.ly.add(ly[i]);
            this.lz.add(lz[i]);
        }
        this.ax = null;
        this.ay = null;
        this.az = null;
        this.gx = null;
        this.gy = null;
        this.gz = null;
        this.user = user;
        this.gestureName = gName;
    }

    public Gesture(float[] lx, float[] ly, float[] lz) {
        boolean flag = (lx.length == ly.length) && (ly.length == lz.length);
        assert flag : "the lenght of lx ly lz must be the same.";
        for (int i = 0; i < lx.length; i++) {
            this.lx.add(lx[i]);
            this.ly.add(ly[i]);
            this.lz.add(lz[i]);
        }
        this.ax = null;
        this.ay = null;
        this.az = null;
        this.gx = null;
        this.gy = null;
        this.gz = null;
        this.user = "unknown";
        this.gestureName = "unknown";
    }

    public Gesture(ArrayList<Float> lx, ArrayList<Float> ly, ArrayList<Float> lz,
                   ArrayList<Float> ax, ArrayList<Float> ay, ArrayList<Float> az,
                   ArrayList<Float> gx, ArrayList<Float> gy, ArrayList<Float> gz,
                   String user, String gName) {
        this.lx = lx;
        this.ly = ly;
        this.lz = lz;
        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
        this.user = user;
        this.gestureName = gName;
    }

    public Gesture(ArrayList<Float> lx, ArrayList<Float> ly, ArrayList<Float> lz,
                   ArrayList<Float> ax, ArrayList<Float> ay, ArrayList<Float> az,
                   ArrayList<Float> gx, ArrayList<Float> gy, ArrayList<Float> gz) {
        this.lx = lx;
        this.ly = ly;
        this.lz = lz;
        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
        this.user = "unknown";
        this.gestureName = "unknown";
    }

    public Gesture(float[] lx, float[] ly, float[] lz,
                   float[] ax, float[] ay, float[] az,
                   float[] gx, float[] gy, float[] gz) {
        boolean flag = (lx.length == ly.length) && (ly.length == lz.length);
        flag &= (ax.length == ay.length) && (ay.length == az.length) &&
                (gx.length == gy.length) && (gy.length == gz.length) &&
                (ax.length == gx.length);
        assert flag : "linear data lengh must be the same, acc and gyro data must be the same";
        for (int i = 0; i < lx.length; i++) {
            this.lx.add(lx[i]);
            this.ly.add(ly[i]);
            this.lz.add(lz[i]);
        }
        for (int i = 0; i < ax.length; i++) {
            this.ax.add(ax[i]);
            this.ay.add(ay[i]);
            this.az.add(az[i]);
            this.gx.add(gx[i]);
            this.gy.add(gy[i]);
            this.gz.add(gz[i]);
        }
        this.user = "unknown";
        this.gestureName = "unknown";
    }

    public Gesture(float[] lx, float[] ly, float[] lz,
                   float[] ax, float[] ay, float[] az,
                   float[] gx, float[] gy, float[] gz,
                   String user, String gName) {
        boolean flag = (lx.length == ly.length) && (ly.length == lz.length);
        flag &= (ax.length == ay.length) && (ay.length == az.length) &&
                (gx.length == gy.length) && (gy.length == gz.length) &&
                (ax.length == gx.length);
        assert flag : "linear data lengh must be the same, acc and gyro data must be the same";
        for (int i = 0; i < lx.length; i++) {
            this.lx.add(lx[i]);
            this.ly.add(ly[i]);
            this.lz.add(lz[i]);
        }
        for (int i = 0; i < ax.length; i++) {
            this.ax.add(ax[i]);
            this.ay.add(ay[i]);
            this.az.add(az[i]);
            this.gx.add(gx[i]);
            this.gy.add(gy[i]);
            this.gz.add(gz[i]);
        }
        this.user = user;
        this.gestureName = gName;
    }

    public ArrayList<Float> getLx() {
        return this.lx;
    }

    public ArrayList<Float> getLy() {
        return this.ly;
    }

    public ArrayList<Float> getLz() {
        return this.lz;
    }

    public ArrayList<Float> getAx() {
        return this.ax;
    }

    public ArrayList<Float> getAy() {
        return this.ay;
    }

    public ArrayList<Float> getAz() {
        return this.az;
    }

    public ArrayList<Float> getGx() {
        return this.gx;
    }

    public ArrayList<Float> getGy() {
        return this.gy;
    }

    public ArrayList<Float> getGz() {
        return this.gz;
    }

    public String getUser() {
        return this.user;
    }

    public String getGestureName() {
        return this.gestureName;
    }

    public void normalization() {
        int n = this.lx.size();
        float sumlx = 0;
        float sumly = 0;
        float sumlz = 0;
        for (int i = 0; i < n; i++) {
            sumlx += this.lx.get(i);
            sumly += this.ly.get(i);
            sumlz += this.lz.get(i);
        }
        float avelx = sumlx / n;
        float avely = sumly / n;
        float avelz = sumlz / n;

        float sqrsumlx = 0;
        float sqrsumly = 0;
        float sqrsumlz = 0;
        for (int i = 0; i < n; i++) {
            sqrsumlx += (lx.get(i) - avelx) * (lx.get(i) - avelx);
            sqrsumly += (ly.get(i) - avely) * (ly.get(i) - avely);
            sqrsumlz += (lz.get(i) - avelz) * (lz.get(i) - avelz);
        }
        float sdivlx = (float) Math.sqrt(sqrsumlx / n);
        float sdivly = (float) Math.sqrt(sqrsumly / n);
        float sdivlz = (float) Math.sqrt(sqrsumlz / n);
        for (int i = 0; i < n; i++) {
            this.lx.set(i, (this.lx.get(i) - avelx) / sdivlx);
            this.ly.set(i, (this.ly.get(i) - avely) / sdivly);
            this.lz.set(i, (this.lz.get(i) - avelz) / sdivlz);
        }
    }

    public void cutOff() {
        float thresholdlxs = 0.3f; // for lx start
        float thresholdlys = 0.3f; // for ly start
        float thresholdlzs = 0.3f; // for lz start
        float thresholdij = 0.2f; // for lxj-lxi
        float thresholdEnd = 0.2f;  // for signal end
        int times = 40;//待调整

        // remove from start of the signal
        while (Math.abs(this.lx.get(0)) < thresholdlxs &&
                Math.abs(this.ly.get(0)) < thresholdlys &&
                Math.abs(this.lz.get(0)) < thresholdlzs) {
            this.lx.remove(0);
            this.ly.remove(0);
            this.lz.remove(0);
        }
        while (Math.abs(this.lx.get(1) - this.lx.get(0)) < thresholdij &&
                Math.abs(this.ly.get(1) - this.ly.get(0)) < thresholdij &&
                Math.abs(this.lz.get(1) - this.lz.get(0)) < thresholdij) {
            this.lx.remove(0);
            this.ly.remove(0);
            this.lz.remove(0);
        }
        //remove the end of the signal
        int i;
        int j = 0;
        for (i = 1; i < this.lx.size(); i++) {
            if (j >= times)
                break;
            if (j > 0 && (Math.abs(this.lx.get(i) - this.lx.get(i - 1)) >= thresholdEnd ||
                    Math.abs(this.ly.get(i) - this.ly.get(i - 1)) >= thresholdEnd ||
                    Math.abs(this.lz.get(i) - this.lz.get(i - 1)) >= thresholdEnd))
                j = 0;
            if (Math.abs(this.lx.get(i) - this.lx.get(i - 1)) < thresholdEnd &&
                    Math.abs(this.ly.get(i) - this.ly.get(i - 1)) < thresholdEnd &&
                    Math.abs(this.lz.get(i) - this.lz.get(i - 1)) < thresholdEnd)
                j++;
        }
        i -= j;
        while (i < this.lx.size()) {
            this.lx.remove(i);
            this.ly.remove(i);
            this.lz.remove(i);
        }
    }

    public void saveToCSV(String fileName) {
        StringBuffer sb = new StringBuffer();
        String title = "user,gestureName,lx,ly,lz,ax,ay,az,gx,gy,gz\n";
        sb.append(title);
        if (this.ax != null) {
            int max = Math.max(this.lx.size(), this.ax.size());
            for (int i = 0; i < max; i++) {
                if (i < this.lx.size() && i < this.ax.size()) {
                    String line = this.user + "," +
                            this.gestureName + "," +
                            this.lx.get(i) + "," +
                            this.ly.get(i) + "," +
                            this.lz.get(i) + "," +
                            this.ax.get(i) + "," +
                            this.ay.get(i) + "," +
                            this.az.get(i) + "," +
                            this.gx.get(i) + "," +
                            this.gy.get(i) + "," +
                            this.gz.get(i) +
                            "\n";
                    sb.append(line);
                } else if (i < this.lx.size() && i >= ax.size()) {
                    String line = this.user + "," +
                            this.gestureName + "," +
                            this.lx.get(i) + "," +
                            this.ly.get(i) + "," +
                            this.lz.get(i) + "," +
                            ",,,,," +
                            "\n";
                    sb.append(line);
                } else {
                    String line = this.user + "," +
                            this.gestureName + "," +
                            ",,," +
                            this.ax.get(i) + "," +
                            this.ay.get(i) + "," +
                            this.az.get(i) + "," +
                            this.gx.get(i) + "," +
                            this.gy.get(i) + "," +
                            this.gz.get(i) +
                            "\n";
                    sb.append(line);
                }
            }
        } else {
            for (int i = 0; i < lx.size(); i++) {
                String line = this.user + "," +
                        this.gestureName + "," +
                        this.lx.get(i) + "," +
                        this.ly.get(i) + "," +
                        this.lz.get(i) + "," +
                        ",,,,," +
                        "\n";
                sb.append(line);
            }
        }
        FileUtils.writeStringToFile(fileName, sb.toString());
    }

    @Override
    public String toString() {
        StringBuffer sbx = new StringBuffer();
        sbx.append("lx: ");
        StringBuffer sby = new StringBuffer();
        sby.append("ly: ");
        StringBuffer sbz = new StringBuffer();
        sbz.append("lz: ");
        for (float x : this.lx) {
            sbx.append(x + ",");
        }
        for (float y : this.ly) {
            sby.append(y + ",");
        }
        for (float z : this.lz) {
            sbz.append(z + ",");
        }
        return sbx.append("\n").toString() + sby.append("\n").toString() + sbz.append("\n").toString();
    }

}
