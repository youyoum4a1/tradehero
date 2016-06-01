package com.ayondo.academy;

import com.ayondo.academy.fragments.DebugFragmentModule;
import dagger.Module;

@Module(
        includes = {
                DebugFragmentModule.class,
        },

        complete = false,
        library = true,
        overrides = true
)
public class BuildTypeUIModule
{
}
