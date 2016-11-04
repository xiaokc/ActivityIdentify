package io.github.zishell.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zishell on 6/16/2015.
 */
public class AppsUtils {
    private static String TAG = "AppsUtils";

    public static ArrayList<String> getUserInstalledApps(Context context) {
        ArrayList<String> packagesArray = new ArrayList<String>();
        try {
            List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                //kick the system apps out
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    packagesArray.add(packageInfo.packageName);
                }
            }
            return packagesArray;
        } catch (Exception e) {
            Log.e(TAG, "=>" + e.toString());
            return packagesArray;
        }

    }

    //get the user app(that installed senz sdk) name
    public static String getPackageInfo(Context context) {
        String packageName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            int versionCode = info.versionCode;
            packageName = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageName = "io.petchat.exception.packagename";
        }
        return packageName;
    }

    /**
     * @param context
     * @return JSONArray ["pa1","p2",...,"pn"]
     */
    public static JSONArray getInstalledApps(Context context) {
        if (context == null) return null;
        JSONArray apps = new JSONArray();
        try {
            List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                //kick the system apps out
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    apps.put(packageInfo.packageName);
                }
            }
            return apps;
        } catch (Exception e) {
            Log.e(TAG, "=>" + e.toString());

            return null;
        }

    }

    public static void saveApps(Context context) {
        if (context == null) return;
        JSONArray apps = AppsUtils.getInstalledApps(context);
        if (apps != null && apps.length() > 0) {
            long ts = System.currentTimeMillis();
            String appsFileName = String.valueOf(ts);
            appsFileName += ".txt";
            //FileUtils.checkFolderExist(SenzConstants.FOLDER_APP);
            //File appFile = new File(SenzConstants.FOLDER_APP + "/" + appsFileName);
            //FileUtils.writeStringToFile(appFile.getAbsolutePath(), apps.toString());
        }
    }


}
