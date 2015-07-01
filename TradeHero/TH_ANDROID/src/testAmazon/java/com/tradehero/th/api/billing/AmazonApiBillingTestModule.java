package com.tradehero.th.api.billing;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                AmazonPurchaseInProcessDTOTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class AmazonApiBillingTestModule
{
}
