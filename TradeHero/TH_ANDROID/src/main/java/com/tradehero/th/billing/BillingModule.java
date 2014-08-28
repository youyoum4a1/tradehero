package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.th.billing.amazon.AmazonBillingModule;
import com.tradehero.th.billing.googleplay.GooglePlayBillingModule;
import com.tradehero.th.billing.samsung.SamsungBillingModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
                //AmazonBillingModule.class,
                GooglePlayBillingModule.class,
                //SamsungBillingModule.class,
        },
        injects = {
        },
        staticInjections = {
        },
        complete = false,
        library = true
)
public class BillingModule
{
    @Provides @Singleton BillingLogicHolder provideBillingActor(THBillingLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton BillingInteractor provideBillingInteractor(THBillingInteractor billingInteractor)
    {
        return billingInteractor;
    }
}
