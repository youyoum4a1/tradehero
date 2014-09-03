package com.tradehero.th.api.billing;

import com.tradehero.th.api.ValidMocker;
import com.tradehero.th.api.ValidMockerAmazon;
import dagger.Module;
import dagger.Provides;

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
