package com.androidth.general;

import com.androidth.general.network.NetworkDebugModule;
import dagger.Module;

@Module(
        includes = {
//                FlavorDebugModule.class,
                NetworkDebugModule.class,
        },

        complete = false,
        library = true,
        overrides = true
)
public class BuildTypeModule
{
}
