package com.tradehero.th;

import com.tradehero.th.network.NetworkDebugModule;
import dagger.Module;


@Module(
        includes = NetworkDebugModule.class,

        complete = false,
        library = true
)
public class DebugModule
{
}
