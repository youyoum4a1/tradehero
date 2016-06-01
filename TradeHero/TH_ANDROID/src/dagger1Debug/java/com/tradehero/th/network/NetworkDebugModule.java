package com.ayondo.academy.network;

import com.ayondo.academy.network.retrofit.RetrofitStubModule;
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
