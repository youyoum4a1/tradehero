package com.androidth.general.api.billing;

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
