package com.ayondo.academy.fragments;

import com.ayondo.academy.fragments.billing.FragmentBillingAppTestModule;
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
