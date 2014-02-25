package com.tradehero.th.billing;

import com.tradehero.th.billing.googleplay.THIABModule;
import dagger.Module;

/**
 * Created by xavier on 2/17/14.
 */
@Module(
        includes = {
                THIABModule.class,
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
    public static final String TAG = BillingModule.class.getSimpleName();

    //@Provides @Singleton THBillingInteractor provideBillingInteractor(BillingLogicHolder billingLogicHolder)
    //{
    //    THBillingInteractor billingInteractor = new THIABUserInteractor();
    //    billingInteractor.setBillingLogicHolder(billingLogicHolder);
    //    return billingInteractor;
    //}
}
