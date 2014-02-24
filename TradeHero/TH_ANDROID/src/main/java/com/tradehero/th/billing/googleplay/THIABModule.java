package com.tradehero.th.billing.googleplay;

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
public class THIABModule
{
    public static final String TAG = THIABModule.class.getSimpleName();

    @Provides @Singleton THIABLogicHolder provideTHIABLogicHolder(THIABLogicHolderFull thiabLogicHolderFull)
    {
        return thiabLogicHolderFull;
    }

    @Provides THIABUserInteractor provideTHIABUserInteractor()
    {
        return new THIABUserInteractor();
    }
}
