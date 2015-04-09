package com.tradehero.th.network;

import com.tradehero.th.network.retrofit.RetrofitTestModule;
import com.tradehero.th.network.service.NetworkServiceTestModule;
import dagger.Module;

@Module(
        includes = {
                RetrofitTestModule.class,
                NetworkServiceTestModule.class,
        },
        library = true,
        complete = false,
        overrides = true
)
public class NetworkTestModule
{
}
