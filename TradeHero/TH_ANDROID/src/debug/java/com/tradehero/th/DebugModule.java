package com.tradehero.th;

import com.tradehero.th.network.NetworkDebugModule;
import dagger.Module;

/**
 * Created by thonguyen on 12/4/14.
 */
@Module(
        includes = NetworkDebugModule.class,

        complete = false,
        library = true
)
public class DebugModule
{
}
