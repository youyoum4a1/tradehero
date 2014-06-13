package com.tradehero.th.activities;

import android.os.Handler;
import android.os.Looper;
import com.tradehero.th.utils.dagger.ForUIThread;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
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

    @Provides @Singleton CurrentActivityHolder provideCurrentActivityHandler(@ForUIThread Handler handler)
    {
        return new CurrentActivityHolder(handler);
    }
}
