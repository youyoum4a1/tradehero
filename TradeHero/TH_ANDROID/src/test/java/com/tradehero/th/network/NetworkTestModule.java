package com.ayondo.academy.network;

import com.ayondo.academy.network.retrofit.RetrofitTestModule;
import com.ayondo.academy.network.service.NetworkServiceTestModule;
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
