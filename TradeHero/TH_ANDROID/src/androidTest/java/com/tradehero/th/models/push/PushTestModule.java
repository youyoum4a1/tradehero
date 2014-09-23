package com.tradehero.th.models.push;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module(
        includes = {
                FlavorPushTestModule.class
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
