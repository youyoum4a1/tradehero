package com.ayondo.academy;

import com.ayondo.academy.network.NetworkDebugModule;
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
