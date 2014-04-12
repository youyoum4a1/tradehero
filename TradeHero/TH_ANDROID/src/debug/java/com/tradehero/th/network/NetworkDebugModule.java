package com.tradehero.th.network;

import com.tradehero.th.network.retrofit.RetrofitStubModule;
import dagger.Module;

/**
 * Created by thonguyen on 12/4/14.
 */
@Module(
        includes = {
                RetrofitStubModule.class
        },

        complete = false,
        library = true
)
public class NetworkDebugModule
{
}
