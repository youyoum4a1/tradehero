package com.tradehero.th.fragments.billing;

import com.tradehero.th.fragments.billing.store.FragmentBillingStoreAppTestModule;
import dagger.Module;

@Module(
        includes = {
                FragmentBillingStoreAppTestModule.class
        },
        injects = {
                StoreItemClickableViewTest.class,
                StoreItemHasFurtherTest.class,
                StoreItemHeaderViewTest.class,
                StoreItemPromptPurchaseTest.class,
        },
        complete = false,
        library = true
)
public class FragmentBillingAppTestModule
{
}
