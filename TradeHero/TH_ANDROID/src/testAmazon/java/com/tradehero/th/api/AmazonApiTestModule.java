package com.androidth.general.api;

import com.androidth.general.api.billing.AmazonApiBillingTestModule;
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
