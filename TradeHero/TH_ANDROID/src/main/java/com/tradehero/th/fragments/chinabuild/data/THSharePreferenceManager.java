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

    private final static String TH_SP_NAME = "th_sp_name_app_version";

    public static void saveValuesByKey(){}

    public static AppInfoDTO getAppVersionInfo(Context context){
        if(context==null){
            return null;
        }
        String url = "";
        boolean suggestUpdate = false;
        boolean forceUpdate = false;
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME,Context.MODE_PRIVATE);
        url = sp.getString(KEY_APP_NEW_VERSION_DOWNLOAD_URL, "");
        suggestUpdate = sp.getBoolean(KEY_APP_SUGGEST_UPDATE, false);
        forceUpdate = sp.getBoolean(KEY_APP_FORCE_UPDATE, false);
        AppInfoDTO dto = new AppInfoDTO();
        dto.setForceUpgrade(forceUpdate);
        dto.setSuggestUpgrade(suggestUpdate);
        dto.setLatestVersionDownloadUrl(url);
        return dto;
    }

    public static void saveUpdateAppUrlLastestVersionCode(Context context, String url, boolean suggestUpdate, boolean forceUpdate){
        if(TextUtils.isEmpty(url)){
            return;
        }
        if(context==null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(TH_SP_NAME,Context.MODE_PRIVATE);
        sp.edit().putString(KEY_APP_NEW_VERSION_DOWNLOAD_URL, url).commit();
        sp.edit().putBoolean(KEY_APP_FORCE_UPDATE, forceUpdate).commit();
        sp.edit().putBoolean(KEY_APP_SUGGEST_UPDATE, suggestUpdate).commit();
    }

}
