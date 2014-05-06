package com.tradehero.th.models.push;

import android.content.Context;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.MetaHelper;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.persistence.prefs.SavedBaiduPushDeviceIdentifier;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import timber.log.Timber;

public class DeviceTokenHelper
{
    @Inject @SavedBaiduPushDeviceIdentifier static StringPreference savedPushDeviceIdentifier;
    @Inject static Context context;

    /**
     * If locale is Chinese, return the token from baidu,otherwise from urbanairship
     * @return
     */
    public static String getDeviceToken()
    {
        boolean isChineseLocale = MetaHelper.isChineseLocale(context.getApplicationContext());
        if (isChineseLocale)
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
        //boolean isChineseLocale = MetaHelper.isChineseLocale(context.getApplicationContext());
        //if (isChineseLocale)
        //{
        //    return DeviceType.Baidu;
        //}
        return DeviceType.Android;
    }
}
