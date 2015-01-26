package com.tradehero.th;

import com.tradehero.th.billing.amazon.AmazonPurchasingServiceDummy;
import dagger.Module;

@Module(
        includes = {
        },
        overrides = true,
        complete = false,
        library = true
)
public class FlavorDebugModule
{
    //@Provides @Singleton AmazonPurchasingService provideAmazonPurchasingService(AmazonPurchasingServiceDummy amazonPurchasingServiceDummy)
    //{
    //    return amazonPurchasingServiceDummy;
    //}
}
