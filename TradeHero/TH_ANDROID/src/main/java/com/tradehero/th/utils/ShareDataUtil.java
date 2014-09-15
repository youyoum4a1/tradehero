package com.tradehero.th.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareDataUtil {

    private static SharedPreferences sp;
    private static final String SP_NAME = "sp_china_build";


    private static void init(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME,0);
        }
    }

    public static void setSharedIntData(Context context, String key, int value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putInt(key, value).commit();
    }

    public static int getSharedIntData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getInt(key, 0);
    }

    public static void setSharedlongData(Context context, String key, long value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putLong(key, value).commit();
    }

    public static long getSharedlongData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getLong(key, 0l);
    }

    public static void setSharedFloatData(Context context, String key,
            float value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putFloat(key, value).commit();
    }

    public static Float getSharedFloatData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getFloat(key, 0f);
    }

    public static void setSharedBooleanData(Context context, String key,
            boolean value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    public static Boolean getSharedBooleanData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getBoolean(key, false);
    }

    public static void setSharedStringData(Context context, String key,
            String value) {
        if (sp == null) {
            init(context);
        }
        sp.edit().putString(key, value).commit();
    }

    public static String getSharedStringData(Context context, String key) {
        if (sp == null) {
            init(context);
        }
        return sp.getString(key, "");
    }
}


