package com.tradehero.th.fragments.billing;

import com.tradehero.th.fragments.billing.store.FragmentBillingStoreTestModule;
import dagger.Module;

@Module(
        includes = {
                FragmentBillingStoreTestModule.class,
        },
        injects = {
                StoreScreenFragmentTest.class
        },
        complete = false,
        library = true
)
public class FragmentBillingTestModule
{
}
