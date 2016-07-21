package com.tradehero.th;

import com.androidth.general.fragments.DebugFragmentModule;
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
