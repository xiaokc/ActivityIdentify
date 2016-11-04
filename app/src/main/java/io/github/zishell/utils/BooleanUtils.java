package io.github.zishell.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zishell on 2015/8/21.
 */
public class BooleanUtils {

    /**
     * check the user id:
     * here just check null and length
     * TODO: maybe there are other rules
     *
     * @param userId
     * @return
     */
    public static boolean checkUserId(String userId) {
        if (userId != null && userId.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * check the user installation id
     * alert null length > 0
     * TODO: other rules
     *
     * @param installationId
     * @return
     */
    public static boolean checkInstallationId(String installationId) {
        if (installationId != null && installationId.length() > 0) {
            return true;
        }
        return false;
    }

    public static boolean isWiFiAvailable(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI")
                            && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // check the network is available( both wifi and mobile data )
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }


}
