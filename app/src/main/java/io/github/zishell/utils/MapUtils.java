package io.github.zishell.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by zishell on 6/24/2015.
 */
public class MapUtils {
    public static String mapToString(HashMap<Object, Object> map) {
        StringBuffer sb = new StringBuffer();
        for (Object key : map.keySet()) {
            Log.i("JsonTest", key + ": " + map.get(key));
            sb.append(key + " : " + map.get(key) + "\n");
        }
        return sb.toString();
    }


    public static HashMap<String, Double> getHashMapFromJson(JSONObject json) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String key = it.next();
            try {
                map.put(key, json.getDouble(key));
            } catch (JSONException e) {
                return null;
            }
        }
        return map;
    }

    public static HashMap<String, Double> getHashMapFromJsonString(String jsonStr) {
        try {
            HashMap<String, Double> map = new HashMap<String, Double>();
            JSONObject json = new JSONObject(jsonStr);
            for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                String key = it.next();
                try {
                    map.put(key, json.getDouble(key));
                } catch (JSONException e) {
                    return null;
                }
            }
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

}
