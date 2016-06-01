package com.ayondo.academy.api.billing;

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
