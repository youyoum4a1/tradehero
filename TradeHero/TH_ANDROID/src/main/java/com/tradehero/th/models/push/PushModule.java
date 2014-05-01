package com.tradehero.th.models.push;

import com.tradehero.th.models.push.baidu.BaiduPushModule;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushModule;
import dagger.Module;

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
}
