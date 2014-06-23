package com.tradehero.th.models.push;

import android.content.Context;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.MarketSegment;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import timber.log.Timber;

public class DeviceTokenHelper
{
    @Inject @SavedPushDeviceIdentifier static StringPreference savedPushDeviceIdentifier;
    @Inject static Context context;

    public static boolean isChineseVersion()
    {
        return Constants.TAP_STREAM_TYPE.marketSegment.equals(MarketSegment.CHINA);
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

    public static DeviceType getDeviceType()
    {
        return Constants.TAP_STREAM_TYPE.marketSegment.deviceType;
    }
}
