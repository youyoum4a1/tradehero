package com.tradehero.th.models.push;

import android.content.Context;
import com.tradehero.common.utils.MetaHelper;
import com.tradehero.th.models.push.baidu.BaiduPushManager;
import com.tradehero.th.models.push.baidu.BaiduPushModule;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushModule;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushNotificationManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Provider;
import javax.inject.Singleton;

@Module(
        includes = {
                BaiduPushModule.class,
                UrbanAirshipPushModule.class
        },
        complete = false,
        library = true
)
public class PushModule
{
    @Provides @Singleton PushNotificationManager providePushNotificationManager(
            Context context,
            Provider<BaiduPushManager> baiduPushManager,
            Provider<UrbanAirshipPushNotificationManager> urbanAirshipPushNotificationManager)
    {
        boolean isChineseLocale = MetaHelper.isChineseLocale(context.getApplicationContext());
        if (isChineseLocale)
        {
            return baiduPushManager.get();
        }
        else
        {
            return urbanAirshipPushNotificationManager.get();
        }
    }
}
