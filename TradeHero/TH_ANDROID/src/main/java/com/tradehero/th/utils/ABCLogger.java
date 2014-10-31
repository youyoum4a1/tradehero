package com.tradehero.th.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by palmer on 14-10-30.
 */
public class ABCLogger {

    private final static boolean isDebug = true;
    private final static String TAG = "abc";

    public static void d(String logStr){
        if(!isDebug){
            return;
        }
        if(TextUtils.isEmpty(logStr)){
            Log.d(TAG, "");
            return;
        }
        Log.d(TAG, logStr);
    }

}
