package com.tradehero.th.models.push;

import android.content.Context;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.persistence.prefs.SavedBaiduPushDeviceIdentifier;
import com.tradehero.th.utils.Constants;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import timber.log.Timber;

public class DeviceTokenHelper
{
    @Inject @SavedBaiduPushDeviceIdentifier static StringPreference savedPushDeviceIdentifier;
    @Inject static Context context;

    public static boolean isChineseVersion()
    {
        //TODO need to improve
        boolean flag = Constants.VERSION > 0;
        //MetaHelper.isChineseLocale(context.getApplicationContext());
        return flag;
    }

    /**
     * If locale is Chinese, return the token from baidu,otherwise from urbanairship
     * @return
     */
    public static String getDeviceToken()
    {
        if (isChineseVersion())
        {
            String token = savedPushDeviceIdentifier.get();
            Timber.d("get saved the token from baidu %s", token);
            return token;
        }
        return PushManager.shared().getAPID();
    }

    /**
     * just return DeviceType.Android
     * @return
     */
    public static DeviceType getDeviceType()
    {
        if (isChineseVersion())
        {
            return DeviceType.ChineseVersion;
        }
        return DeviceType.Android;
    }
}
