package com.tradehero.th.activities;

import android.os.Handler;
import android.os.Looper;
import com.tradehero.th.utils.dagger.ForUIThread;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by xavier on 2/11/14.
 */
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
    public static final String TAG = ActivityModule.class.getSimpleName();

    @Provides @ForUIThread Handler provideUIHandler()
    {
        return new Handler(Looper.getMainLooper());
    }

    @Provides @Singleton CurrentActivityHolder provideCurrentActivityHandler(@ForUIThread Handler handler)
    {
        return new CurrentActivityHolder(handler);
    }
}
