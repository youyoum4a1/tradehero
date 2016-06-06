package com.androidth.general.common.utils;

import android.os.Build;

public class SDKUtils
{
    public static boolean isHoneycombOrHigher()
    {
        return isDeviceSDKGreaterOrEqualThan(Build.VERSION_CODES.HONEYCOMB);
    }

    public static boolean isICSOrHigher()
    {
        return isDeviceSDKGreaterOrEqualThan(Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    public static boolean isKitKatOrHigher()
    {
        return isDeviceSDKGreaterOrEqualThan(Build.VERSION_CODES.KITKAT);
    }

    public static boolean isLollipopOrHigher()
    {
        return isDeviceSDKGreaterOrEqualThan(Build.VERSION_CODES.LOLLIPOP);
    }

    public static boolean isJellyBeanOrHigher()
    {
        return isDeviceSDKGreaterOrEqualThan(Build.VERSION_CODES.JELLY_BEAN);
    }

    public static boolean isDeviceSDKGreaterOrEqualThan(int sdkInt)
    {
        return Build.VERSION.SDK_INT >= sdkInt;
    }
}
