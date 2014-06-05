package com.tradehero.th;

import com.tradehero.th.api.ApiModule;
import dagger.Module;

@Module(
        includes = {
                ApiModule.class
        },
        complete = false,
        library = true
)
public class AppModule
{
}
