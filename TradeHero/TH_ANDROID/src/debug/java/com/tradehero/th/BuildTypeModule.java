package com.tradehero.th;

import com.tradehero.th.network.NetworkDebugModule;
import dagger.Module;

@Module(
        includes = {
                FlavorDebugModule.class,
                NetworkDebugModule.class,
        },

        complete = false,
        library = true,
        overrides = true
)
public class BuildTypeModule
{
}
