package com.tradehero.th;

import com.tradehero.th.fragments.ReleaseFragmentModule;
import dagger.Module;

@Module(
        includes = {
                ReleaseFragmentModule.class,
        },

        complete = false,
        library = true,
        overrides = true
)
public class BuildTypeUIModule
{
}
