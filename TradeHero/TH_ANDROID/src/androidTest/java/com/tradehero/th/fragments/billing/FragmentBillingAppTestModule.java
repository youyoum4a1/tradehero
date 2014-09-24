package com.tradehero.th.fragments.billing;

import com.tradehero.th.fragments.billing.store.FragmentBillingStoreAppTestModule;
import dagger.Module;

@Module(
        includes = {
                FragmentBillingStoreAppTestModule.class
        },
        injects = {
                StoreItemClickableTest.class,
                StoreItemHasFurtherTest.class,
                StoreItemHeaderTest.class,
                StoreItemPromptPurchaseTest.class,
        },
        complete = false,
        library = true
)
public class FragmentBillingAppTestModule
{
}
