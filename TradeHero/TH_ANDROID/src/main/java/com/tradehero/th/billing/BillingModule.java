package com.tradehero.th.billing;

import com.tradehero.th.billing.googleplay.GooglePlayBillingModule;
import dagger.Module;

@Module(
        includes = {
                GooglePlayBillingModule.class,
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
