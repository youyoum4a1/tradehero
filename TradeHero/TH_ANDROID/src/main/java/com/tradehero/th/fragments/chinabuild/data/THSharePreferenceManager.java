package com.tradehero.th.fragments.chinabuild.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by palmer on 14-10-21.
 */
public class THSharePreferenceManager {

    public final static String KEY_APP_NEW_VERSION_DOWNLOAD_URL = "key_app_new_version_download_url";
    public final static String KEY_APP_SUGGEST_UPDATE = "key_app_suggest_update";
    public final static String KEY_APP_FORCE_UPDATE = "key_app_force_update";
    public final static String KEY_APP_NOTIFICATION_ON_OFF = "key_app_notification_on_off";
    public final static String KEY_SIGN_IN_ACCOUNT = "key_sign_in_account";

    private final static String TH_SP_NAME = "th_sp_name_app_version";


    public final static String PROPERTY_MORE_THAN_FIFTEEN = "property_more_than_fifteen";
    public final static String PROPERTY_MORE_THAN_TWENTY_FIVE = "property_more_than_twenty_five";

    public static void saveValuesByKey() {
    }

    public static AppInfoDTO getAppVersionInfo(Context context) {
        if (context == null) {
            return null;
        }
        String url = "";
        boolean suggestUpdate = false;
        boolean forceUpdate = false;
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        url = sp.getString(KEY_APP_NEW_VERSION_DOWNLOAD_URL, "");
        suggestUpdate = sp.getBoolean(KEY_APP_SUGGEST_UPDATE, false);
        forceUpdate = sp.getBoolean(KEY_APP_FORCE_UPDATE, false);
        AppInfoDTO dto = new AppInfoDTO();
        dto.setForceUpgrade(forceUpdate);
        dto.setSuggestUpgrade(suggestUpdate);
        dto.setLatestVersionDownloadUrl(url);
        return dto;
    }

    public static void saveUpdateAppUrlLastestVersionCode(Context context, String url, boolean suggestUpdate, boolean forceUpdate) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_APP_NEW_VERSION_DOWNLOAD_URL, url).commit();
        sp.edit().putBoolean(KEY_APP_FORCE_UPDATE, forceUpdate).commit();
        sp.edit().putBoolean(KEY_APP_SUGGEST_UPDATE, suggestUpdate).commit();
    }

    public static boolean isNotificationsOn(Context context) {
        if (context == null) {
            return true;
        }
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        boolean isOn = sp.getBoolean(KEY_APP_NOTIFICATION_ON_OFF, true);
        return isOn;
    }

    public static void setNotificaitonsStatus(Context context, boolean notificationsStatus) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_APP_NOTIFICATION_ON_OFF, notificationsStatus).commit();
    }

    public static void saveAccount(Context context, String account) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_SIGN_IN_ACCOUNT, account).commit();
    }

    public static String getAccount(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_SIGN_IN_ACCOUNT, "");
    }

    public static void recordShareDialogMoreThanFifteen(int userId, boolean isConfirm, Context context) {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        if (isConfirm) {
            sp.edit().putInt(userId + "true" + PROPERTY_MORE_THAN_FIFTEEN, 1).commit();
        } else {
            int cancelRecord = sp.getInt(userId + "false" + PROPERTY_MORE_THAN_FIFTEEN, 0);
            cancelRecord++;
            sp.edit().putInt(userId + "false" + PROPERTY_MORE_THAN_FIFTEEN, cancelRecord).commit();
        }
    }

    public static boolean isShareDialogMoreThanFifteenAvailable(int userId, Context context) {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        int confirmRecord = sp.getInt(userId + "true" + PROPERTY_MORE_THAN_FIFTEEN, 0);
        if (confirmRecord > 0) {
            return false;
        }
        int cancelRecord = sp.getInt(userId + "false" + PROPERTY_MORE_THAN_FIFTEEN, 0);
        if (cancelRecord >= 3) {
            return false;
        }
        return true;
    }

    public static void recordShareDialogMoreThanTwentyFive(int userId, boolean isConfirm, Context context) {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        if (isConfirm) {
            sp.edit().putInt(userId + "true" + PROPERTY_MORE_THAN_TWENTY_FIVE, 1).commit();
        } else {
            int cancelRecord = sp.getInt(userId + "false" + PROPERTY_MORE_THAN_TWENTY_FIVE, 0);
            cancelRecord++;
            sp.edit().putInt(userId + "false" + PROPERTY_MORE_THAN_TWENTY_FIVE, cancelRecord).commit();
        }
    }

    public static boolean isShareDialogMoreThanTwentyFiveAvailable(int userId, Context context) {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME, Context.MODE_PRIVATE);
        int confirmRecord = sp.getInt(userId + "true" + PROPERTY_MORE_THAN_TWENTY_FIVE, 0);
        if (confirmRecord > 0) {
            return false;
        }
        int cancelRecord = sp.getInt(userId + "false" + PROPERTY_MORE_THAN_TWENTY_FIVE, 0);
        if (cancelRecord >= 3) {
            return false;
        }
        return true;
    }

}
