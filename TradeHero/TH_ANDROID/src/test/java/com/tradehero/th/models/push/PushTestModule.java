package com.tradehero.th.models.push;

import com.tradehero.th.models.push.baidu.BaiduTestModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module(
        includes = {
                BaiduTestModule.class
        },
        complete = false,
        library = true,
        overrides = true
)
public class PushTestModule
{
    @Provides @Singleton PushNotificationManager providePushNotificationManager()
    {
        return mock(PushNotificationManager.class);
    }
}
