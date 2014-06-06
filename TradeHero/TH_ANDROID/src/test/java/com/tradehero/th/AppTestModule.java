package com.tradehero.th;

import com.tradehero.th.api.ApiModule;
import com.tradehero.th.fragments.FragmentTestModule;
import com.tradehero.th.models.ModelsTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiModule.class,
                ModelsTestModule.class,
                FragmentTestModule.class
        },
        complete = false,
        library = true
)
public class AppTestModule
{
}
