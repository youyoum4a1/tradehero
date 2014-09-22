package com.tradehero.th.models.push;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

    @Provides DeviceTokenHelper providesDeviceTokenHelper(
            @NotNull DeviceTokenHelperDummy deviceTokenHelperDummy)
    {
        return deviceTokenHelperDummy;
    }
}
