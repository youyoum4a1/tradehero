package com.tradehero.th.fragments;

import com.tradehero.th.fragments.security.SecurityTestModule;
import dagger.Module;

@Module(
        includes = {
                SecurityTestModule.class
        },
        complete = false,
        library = true
)
public class FragmentTestModule
{
}
