package com.ayondo.academy.api;

import com.ayondo.academy.api.billing.AmazonApiBillingTestModule;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                AmazonApiBillingTestModule.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class AmazonApiTestModule
{
    @Provides ValidMocker provideValidMocker(ValidMockerAmazon validMocker)
    {
        return validMocker;
    }
}
