package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.localytics.android.LocalyticsSession;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/7/14 Time: 11:22 AM Copyright (c) TradeHero
 */
@Module(
        complete = false,
        library = true
)
public class UxModule
{
    @Provides @Singleton LocalyticsSession provideLocalyticsSession(Context context)
    {
        return new LocalyticsSession(context);
    }
}
