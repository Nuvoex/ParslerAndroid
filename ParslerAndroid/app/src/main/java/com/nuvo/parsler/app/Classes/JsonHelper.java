package com.nuvo.parsler.app.Classes;

import android.util.Log;

import com.google.gson.JsonObject;

/**
 * Created by subodh on 30/6/14.
 */
public class JsonHelper {
    private static final String TAG = "JsonHelper";

    public static String getValueOrNone(JsonObject jsonObject, String key){
        try{
            return jsonObject.get(key).getAsString();
        }catch(Exception e){
            Log.d(TAG, e.toString());
            return "NA";
        }
    }
}
