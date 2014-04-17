package com.tradehero.th.utils.dagger;

import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.models.push.baidu.BaiduPushMessageReceiver;
import com.tradehero.th.utils.ForBaiduPush;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:44 AM Copyright (c) TradeHero
 */
@Module(
        injects = {
                BaiduPushMessageReceiver.class,
        },
        staticInjections = {
                DeviceTokenHelper.class
        },
        complete = false,
        library = true
)
public class PushModule
{
    private static final String BAIDU_API_KEY = "iI9WWqP3SfGApTW37UuSyIdc";

    @Provides @Singleton @ForBaiduPush String provideBaiduAppKey()
    {
        return BAIDU_API_KEY;
    }
}
