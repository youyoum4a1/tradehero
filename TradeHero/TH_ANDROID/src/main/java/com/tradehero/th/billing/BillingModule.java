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
}
