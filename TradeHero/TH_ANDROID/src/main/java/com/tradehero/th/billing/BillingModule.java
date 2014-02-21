package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingActor;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by xavier on 2/17/14.
 */
@Module(
        injects = {
        },
        staticInjections = {
        },
        complete = false,
        library = true
)
public class BillingModule
{
    public static final String TAG = BillingModule.class.getSimpleName();

    @Provides @Singleton
    BillingActor provideBillingActor(CurrentActivityHolder currentActivityHolder)
    {
        return new THIABLogicHolder(currentActivityHolder.getCurrentActivity());
    }
}
