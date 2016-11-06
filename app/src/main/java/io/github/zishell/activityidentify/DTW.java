package io.github.zishell.activityidentify;

import android.os.Environment;

import com.fastdtw.dtw.FastDTW;
import com.fastdtw.dtw.window.SearchWindow;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesBase;
import com.fastdtw.util.Distances;

import java.util.ArrayList;

import io.github.zishell.activityidentify.model.Gesture;

/**
 * Created by zishe on 2016/11/5.
 */

public class DTW {
    public static float getSignalDistance(final ArrayList<Float> inputSignal, final ArrayList<Float> patternSignal) {
        int m = patternSignal.size();
        int n = inputSignal.size();
        if (2 * m - n < 3 || 2 * n - m < 2) return 100f; //数据长度相差很大
        float[] D = new float[m];
        float[] d = new float[m];
        int[] number = new int[m];
        int[] num = new int[m];
        for (int i = 0; i < m; i++) {
            D[i] = Float.POSITIVE_INFINITY;
            d[i] = 0;
            number[i] = 0;
            num[i] = 0;
        }
        D[0] = distanceFunc(inputSignal.get(0), patternSignal.get(0));
        D[1] = distanceFunc(inputSignal.get(0), patternSignal.get(1));
        number[0] = number[1] = 1;
        int xa = (2 * m - n) / 3;
        int xb = 2 * (2 * n - m) / 3;
        for (int x1 = 1; x1 < n; x1++) {
            int ymin, ymax;
            if (x1 + 1 <= xb) {
                if (x1 % 2 == 0)
                    ymin = (x1 + 1) / 2;
                else
                    ymin = (x1 + 1) / 2 - 1;
            } else {
                ymin = 2 * (x1 + 1) + (m - 2 * n) - 1;
            }
            if (x1 + 1 <= xa)
                ymax = 2 * (x1 + 1) - 1;
            else
                ymax = (x1 + 1) / 2 + (m - n / 2) - 1;
            for (int y1 = ymin; y1 <= ymax; y1++) {
                float dis = distanceFunc(inputSignal.get(x1), patternSignal.get(y1));
                float min;
                if (y1 >= 2) {
                    if (D[y1] < D[y1 - 1]) {
                        if (D[y1] < D[y1 - 2])
                            min = D[y1];
                        else {
                            min = D[y1 - 2];
                            num[y1] = number[y1 - 2] + 1;
                        }

                    } else {
                        if (D[y1 - 1] < D[y1 - 2]) {
                            min = D[y1 - 1];
                            num[y1] = number[y1 - 1] + 1;
                        } else {
                            min = D[y1 - 2];
                            num[y1] = number[y1 - 2] + 1;
                        }
                    }
                } else if (y1 >= 1) {
                    if (D[y1] < D[y1 - 1])
                        min = D[y1];
                    else {
                        min = D[y1 - 1];
                        num[y1] = number[y1 - 1] + 1;
                    }
                } else
                    min = D[y1];
                d[y1] = dis + min;
            }
            for (int y1 = 0; y1 < m; y1++)
                D[y1] = Float.POSITIVE_INFINITY;
            for (int y1 = ymin; y1 <= ymax; y1++) {
                D[y1] = d[y1];
                number[y1] = num[y1];
            }
        }
        return D[m - 1] / number[m - 1];
    }

    public static float getGestureDistance(final Gesture inputGesture, final Gesture patternGesture) {
        return (getSignalDistance(inputGesture.getLx(), patternGesture.getLx()) +
                getSignalDistance(inputGesture.getLy(), patternGesture.getLy()) +
                getSignalDistance(inputGesture.getLz(), patternGesture.getLz())) / 3;
    }

    public static float getBaseGestureDistance(final Gesture inputGesture, final Gesture patternGesture) {
        return (getBaseSignalDistance(inputGesture.getLx(), patternGesture.getLx()) +
                getBaseSignalDistance(inputGesture.getLy(), patternGesture.getLy()) +
                getBaseSignalDistance(inputGesture.getLz(), patternGesture.getLz())) / 3;
    }

    private static float distanceFunc(float f1, float f2) {
        return (float) Math.sqrt((f1 - f2) * (f1 - f2));
    }

    private static float minDist(float dist1, float dist2, float dist3) {
        return Math.min(dist3, Math.min(dist1, dist2));
    }

    public static float getBaseSignalDistance(final ArrayList<Float> inputSignal, final ArrayList<Float> patternSignal) {
        float[][] d = new float[inputSignal.size()][patternSignal.size()];
        float[][] D = new float[inputSignal.size()][patternSignal.size()];
        int m = patternSignal.size();
        int n = inputSignal.size();
        int[][] warpingPath = new int[n + m][2];        // max(n, m) <= K < n + m
        int k = 1; //path length
        for (int i = 0; i < inputSignal.size(); i++)
            for (int j = 0; j < patternSignal.size(); j++)
                d[i][j] = distanceFunc(inputSignal.get(i), patternSignal.get(j));

        //根据distance数组来初始化dtw数组
        D[0][0] = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                //边界值的考虑
                if (i > 0 && j > 0) {
                    D[i][j] = d[i][j] + Math.min(Math.min(D[i - 1][j], D[i - 1][j - 1]), D[i][j - 1]);
                } else if (i == 0 && j > 0) {
                    D[0][j] = d[0][j] + D[0][j - 1];
                } else if (i > 0 && j == 0) {
                    D[i][0] = d[i][0] + D[i - 1][0];
                } else {
                    D[i][j] = 0;
                }
            }
        }
        //min_dis = D[n - 1][m - 1];
        // path
        int i = n - 1;
        int j = m - 1;
        int minIndex = 1;

        warpingPath[k - 1][0] = i;
        warpingPath[k - 1][1] = j;

        while ((i + j) != 0) {
            if (i == 0) {
                j -= 1;
            } else if (j == 0) {
                i -= 1;
            } else {        // i != 0 && j != 0
                float[] array = {D[i - 1][j], D[i][j - 1], D[i - 1][j - 1]};
                minIndex = getIndexOfMinimum(array);
                if (minIndex == 0) {
                    i -= 1;
                } else if (minIndex == 1) {
                    j -= 1;
                } else if (minIndex == 2) {
                    i -= 1;
                    j -= 1;
                }
            }
            k++;
            warpingPath[k - 1][0] = i;
            warpingPath[k - 1][1] = j;
        }
        // reverse path
        int[][] newPath = new int[k][2];
        for (int p = 0; p < k; p++) {
            for (int q = 0; q < 2; q++) {
                newPath[p][q] = warpingPath[k - p - 1][q];
            }
        }
        warpingPath = newPath;// for future use
        System.out.println(D[n - 1][m - 1]);
        float warpingDistance = D[n - 1][m - 1] / k;
        return warpingDistance;
    }

    private static int getIndexOfMinimum(float[] array) {
        int index = 0;
        float val = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < val) {
                val = array[i];
                index = i;
            }
        }
        return index;
    }

    public static void main(String[] args) {
        float[] x = {1f, 5f, 3f, 6f, 5f, 4f, 2f};
        float[] y = {0f, 2f, 3f, 4f, 4f, 2f, 1f, 1f, 0f};
        ArrayList<Float> lx = new ArrayList<>();
        ArrayList<Float> ly = new ArrayList<>();
        for (float xi : x) lx.add(xi);
        for (float yi : y) ly.add(yi);
        float d1 = getSignalDistance(lx, ly);
        float d2 = getBaseSignalDistance(lx, ly);
        TimeSeries ts1 = TimeSeriesBase.builder()
                .add(0, 1.0)
                .add(1, 5.0)
                .add(2, 9.0)
                .add(3, 6f)
                .add(4, 5f)
                .add(5, 4f)
                .add(6, 2f).build();
        TimeSeries ts2 = TimeSeriesBase.builder()
                .add(0, 0.0)
                .add(1, 2)
                .add(2, 3.0)
                .add(3, 4)
                .add(4, 8)
                .add(5, 6)
                .add(6, 5)
                .add(7, 2)
                .add(8, 0)
                .build();
        double d3 = com.fastdtw.dtw.DTW.distanceBetween(ts1, ts2, Distances.EUCLIDEAN_DISTANCE);
        System.out.println("d1: " + d1);
        System.out.println("d2: " + d2);
        System.out.println("d3: " + d3);
    }

}
