package com.tradehero.th.utils;

import android.content.SharedPreferences;
import com.tradehero.th.persistence.prefs.PreferenceModule;

/**
 * Created by huhaiping on 14-7-8.
 */
public class SocialSharePreferenceHelper
{
    public static final String PREFERENCE_NAME = PreferenceModule.PREF_SOCIAL_SHARE_FLAG;

    public static final String FB = "fb";
    public static final String LN = "ln";
    public static final String TW = "tw";
    public static final String WB = "wb";
    public static final String WX = "wx";

    public static void saveValue(SharedPreferences preferences, String key, Boolean value)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
}
