package com.tradehero.th.fragments;

import com.tradehero.th.fragments.billing.FragmentBillingAppTestModule;
import dagger.Module;

@Module(
        includes = {
                FragmentBillingAppTestModule.class,
        },
        complete = false,
        library = true
)
public class FragmentAppTestModule
{
}
