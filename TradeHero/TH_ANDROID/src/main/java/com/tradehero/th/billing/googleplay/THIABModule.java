package com.tradehero.th.billing.googleplay;

import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.fragments.billing.THIABUserInteractor;
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

    @Provides @Singleton THIABLogicHolderFull provideTHIABLogicHolder(CurrentActivityHolder currentActivityHolder)
    {
        return new THIABLogicHolderFull(currentActivityHolder.getCurrentActivity());
    }

    @Provides @Singleton THIABLogicHolder provideTHIABActor(THIABLogicHolderFull thiabLogicHolder)
    {
        return thiabLogicHolder;
    }

    @Provides THIABUserInteractor provideTHIABUserInteractor()
    {
        return new THIABUserInteractor();
    }
}
