package com.tradehero.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MetaHelper
{

    @SuppressLint("NewApi")
    public static int[] getScreensize(Context ctx)
    {
        int wh[] = new int[2];
        WindowManager manager = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE));

        Display d = manager.getDefaultDisplay();
        wh[0] = d.getWidth();
        wh[1] = d.getHeight();
        return wh;
    }

    /**
     * 设备品牌（制造商）
     */
    public static String getPhoneBrand()
    {
        return android.os.Build.BRAND;
    }

    /**
     * 设备型号
     */
    public static String getPhoneModel()
    {
        return android.os.Build.MODEL;
    }

    /**
     * Unreliable for CDMA phones
     */
    public static String getNetworkOperator(Context context)
    {
        String ret = null;
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != telephonyManager)
        {
            ret = telephonyManager.getNetworkOperatorName();
        }

		/*
         * int simState = telephonyManager.getSimState();
		 * if(TelephonyManager.SIM_STATE_READY == simState){ String simOperator
		 * = telephonyManager.getSimOperator();
		 * telephonyManager.getSimCountryIso() }
		 */
        return ret;
    }

    /**
     * context.getPackageManager().checkPermission(paramString, context.getPackageName()) == 0
     * android.permission.READ_PHONE_STATE
     */
    public static String getImsi(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        StringBuffer ImsiStr = new StringBuffer();
        try
        {
            ImsiStr.append(tm.getSubscriberId() == null ? "" : tm.getSubscriberId());
            while (ImsiStr.length() < 15)
            {
                ImsiStr.append("0");
            }
        }
        catch (Exception e)
        {
            ImsiStr.append("000000000000000");
            e.printStackTrace();
        }
        return ImsiStr.toString();
    }

    /**
     * context.getPackageManager().checkPermission(paramString, context.getPackageName()) == 0
     * android.permission.READ_PHONE_STATE
     */
    public static String getImei(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        StringBuffer tmDevice = new StringBuffer();
        try
        {
            tmDevice.append(tm.getDeviceId());
            while (tmDevice.length() < 15)
            {
                tmDevice.append("0");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return tmDevice.toString().replace("null", "0000");
    }

    // 设备id
    public static String getAndroidId(Context ctx)
    {
        String str = null;
        try
        {
            str = Settings.Secure.getString(ctx.getContentResolver(), "android_id");
        }
        catch (Exception e1)
        {
        }
        if (str == null)
        {
            try
            {
                str = Settings.System.getString(ctx.getContentResolver(), "android_id");
            }
            catch (Exception e2)
            {
            }
        }
        if (str == null)
        {
            TelephonyManager tm =
                    (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            str = tm.getDeviceId();
        }
        return str;
    }

    // MAC Address
    public static String getMACAddress(Context ctx)
    {
        try
        {
            WifiManager wifiMan = (WifiManager) ctx.getSystemService("wifi");
            String mac = wifiMan.getConnectionInfo().getMacAddress();
			/*
			 * if ((mac == null) || (mac.equals(""))) { return null; } String[]
			 * macParts = mac.split(":"); byte[] macAddress = new byte[6]; for
			 * (int i = 0; i < macParts.length; ++i) { Integer hex = Integer
			 * .valueOf(Integer.parseInt(macParts[i], 16)); macAddress[i] =
			 * hex.byteValue(); } return macAddress;
			 */
            return mac;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getOsVersion(Context ctx)
    {
        return android.os.Build.VERSION.RELEASE + "_" + android.os.Build.VERSION.SDK_INT;
    }

    public static String getTimeZone(Context ctx)
    {
        Configuration configuration = ctx.getResources().getConfiguration();
        Calendar calendar = Calendar.getInstance(configuration.locale);
        TimeZone timeZone = calendar.getTimeZone();
        if (timeZone == null)
        {
            timeZone = TimeZone.getDefault();
        }
        String name = timeZone.getID() + "/" + timeZone.getDisplayName();
        return name;
    }

    public static int getTimeZoneId(Context ctx)
    {
        Configuration configuration = ctx.getResources().getConfiguration();
        Calendar calendar = Calendar.getInstance(configuration.locale);
        TimeZone timeZone = calendar.getTimeZone();
        if (timeZone == null)
        {
            timeZone = TimeZone.getDefault();
        }
        String id = timeZone.getID();
        return timeZone.getRawOffset() / (60 * 60 * 1000);
    }

    public static String getLanguage(Context ctx)
    {
        Configuration configuration = ctx.getResources().getConfiguration();
        //Log.d("Configuration"," configuration.locale "+configuration.locale+" "+configuration.locale.getDisplayName());
        String displayName = configuration.locale.toString();
        //string like '中文 (中国)'
        //configuration.locale.getDisplayName();
        if (displayName == null)
        {
            displayName = Locale.getDefault().toString();
        }
        return displayName;
    }

    public static boolean isChineseLocale(Context context)
    {
        try
        {
            Locale locale = context.getResources().getConfiguration().locale;
            if ((Locale.CHINA.equals(locale)) ||
                    (Locale.CHINESE.equals(locale)) ||
                    (Locale.SIMPLIFIED_CHINESE.equals(locale))
                    /*||(Locale.TAIWAN.equals(locale))*/)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            return true;
        }
        return false;

        //String language = MetaHelper.getLanguage(context);
        //Timber.d("language %s", language);
        //if (language != null && language.equalsIgnoreCase("zh")) {
        //}
    }

    public static String getAppName(Context ctx)
    {
        PackageInfo packageInfo = null;
        try
        {
            packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return packageInfo.packageName;
    }

	/*
	 * public static String getLabel(Context ctx) { return
	 * labels.get(ctx.getClass()) ; } public static void putLabel(){ } static
	 * Map<Class<?>, String> labels = new HashMap<Class<?>, String>();
	 */

    public static boolean isActivityFront(Activity activity)
    {

        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTasks = am.getRunningTasks(30);
        String myPkg = activity.getPackageName();
        if (!runningTasks.isEmpty())
        {
            RunningTaskInfo topTask = runningTasks.get(0);
            android.content.ComponentName topActivity = topTask.topActivity;
            String packageName = topActivity.getPackageName();
            if (packageName.equals(myPkg) && topActivity.getClassName()
                    .equals(activity.getClass().getName()))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isApplicationFront(Context context)
    {

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
        {
            return false;
        }
        final String packageName = context.getPackageName();
        for (RunningAppProcessInfo appProcess : appProcesses)
        {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param context
     * @return
     */
    public static Location getLocation(Context context)
    {
        try
        {
            Location location = null;
            LocationManager lm =
                    (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // android.permission.ACCESS_FINE_LOCATION
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null)
            {
                return location;
            }
            // android.permission.ACCESS_COARSE_LOCATION
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null)
            {
                return location;
            }
            Criteria criteria = new Criteria();
            criteria.setAccuracy(1);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(1);

            String provider = lm.getBestProvider(criteria, true);
            if (provider != null && provider.length() > 0)
            {
                location = lm.getLastKnownLocation(provider);
            }
            return location;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context)
    {
        boolean ret = false;
        ConnectivityManager conMgr = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr != null)
        {
            NetworkInfo i = conMgr.getActiveNetworkInfo();
            if (i != null && i.isConnected() && i.isAvailable())
            {
                ret = true;
            }
        }
        return ret;
    }
}
