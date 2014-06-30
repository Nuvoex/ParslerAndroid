package com.nuvo.parsler.app.Classes;

import android.util.Log;

/**
 * Created by subodh on 29/6/14.
 */
public class ResponseCodes {
    private static final String TAG = "ResponseCodes";
    public static final int CODE_200 = 0;
    public static final int CODE_403 = 1;
    public static final int CODE_404 = 2;
    public static final int CODE_500 = 3;
    public static final int CODE_ERROR = 4;

    public static int getResponseCode(String code){
        if (code.equals("200")) {
            return CODE_200;
        }
        else if (code.equals("404")){
            return CODE_404;
        }
        else if (code.equals("403")){
            return CODE_403;
        }
        else if (code.equals("500")){
            return CODE_500;
        }
        else {
            Log.e(TAG, "Response Code error. Something went wrong with the response codes");
            Log.d(TAG, code);
            return CODE_ERROR;
        }
    }
}