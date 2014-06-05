package com.tradehero.th;

import com.tradehero.th.api.ApiModule;
import com.tradehero.th.models.ModelsTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiModule.class,
                ModelsTestModule.class
        },
        complete = false,
        library = true
)
public class AppModule
{
}
