package com.tradehero.th.activities;

import android.os.Handler;
import android.os.Looper;
import com.tradehero.th.utils.dagger.ForUIThread;
import com.tradehero.th.wxapi.WXEntryActivity;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DashboardActivity.class,
                AuthenticationActivity.class,
                WXEntryActivity.class,
                SplashActivity.class,
        },
        staticInjections = {
        },
        complete = false,
        library = true
)
public class ActivityModule
{
    @Provides @ForUIThread Handler provideUIHandler()
    {
        return new Handler(Looper.getMainLooper());
    }
}
