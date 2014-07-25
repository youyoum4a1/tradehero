package com.tradehero.th.billing;

import com.tradehero.th.billing.googleplay.GooglePlayBillingModule;
import com.tradehero.th.billing.samsung.SamsungBillingModule;
import dagger.Module;

@Module(
        includes = {
                GooglePlayBillingModule.class,
                SamsungBillingModule.class
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
}
