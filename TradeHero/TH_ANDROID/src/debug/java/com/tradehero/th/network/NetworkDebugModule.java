package com.tradehero.th.network;

import com.tradehero.th.network.retrofit.RetrofitStubModule;
import dagger.Module;


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
