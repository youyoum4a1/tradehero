package com.tradehero.th.activities;

import com.tradehero.th.wxapi.WXEntryActivity;
import dagger.Module;

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
}
