package com.tradehero;

import com.ayondo.academy.SamsungAppTestModule;
import dagger.Module;

@Module(
        includes = {
                SamsungAppTestModule.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class FlavorAppTestModule
{
}
