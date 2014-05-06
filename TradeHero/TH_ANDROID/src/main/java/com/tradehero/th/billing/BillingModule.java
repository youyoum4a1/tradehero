package com.tradehero.th.billing;

import com.tradehero.th.billing.googleplay.THIABModule;
import dagger.Module;

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
}
