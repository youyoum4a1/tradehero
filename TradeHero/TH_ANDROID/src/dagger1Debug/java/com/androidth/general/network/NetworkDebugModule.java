package com.androidth.general.network;

import com.androidth.general.network.retrofit.RetrofitStubModule;
import dagger.Module;

@Module(
        includes = {
                RetrofitStubModule.class
        },

        complete = false,
        library = true,
        overrides = true
)
public class NetworkDebugModule
{
}
