package com.tradehero.th.models.push.baidu;

import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.utils.ForBaiduPush;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by thonguyen on 26/4/14.
 */
@Module(
        injects = {
                BaiduPushMessageReceiver.class,
        },
        // TODO remove static injection
        staticInjections = {
                DeviceTokenHelper.class,
                BaiduPushMessageReceiver.class
        },
        complete = false,
        library = true
)
public class BaiduPushModule
{
    private static final String BAIDU_API_KEY = "iI9WWqP3SfGApTW37UuSyIdc";

    @Provides @Singleton @ForBaiduPush String provideBaiduAppKey()
    {
        return BAIDU_API_KEY;
    }
}
