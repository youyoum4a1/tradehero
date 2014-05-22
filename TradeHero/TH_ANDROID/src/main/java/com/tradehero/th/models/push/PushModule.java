package com.tradehero.th.models.push;

import android.content.Context;
import com.tradehero.th.models.push.baidu.BaiduPushManager;
import com.tradehero.th.models.push.baidu.BaiduPushModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Provider;
import javax.inject.Singleton;

@Module(
        includes = {
                BaiduPushModule.class,
                //UrbanAirshipPushModule.class
        },
        complete = false,
        library = true
)
public class PushModule
{
    @Provides @Singleton PushNotificationManager providePushNotificationManager(
            Context context,
            Provider<BaiduPushManager> baiduPushManager)
    {
        boolean isChineseVersion = DeviceTokenHelper.isChineseVersion();
        if (isChineseVersion)
        {
            return baiduPushManager.get();
        }
        return baiduPushManager.get();
    }
}
