package com.tradehero.th;

import com.tradehero.common.billing.amazon.AmazonPurchasingService;
import com.tradehero.th.billing.amazon.AmazonPurchasingServiceDummy;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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
