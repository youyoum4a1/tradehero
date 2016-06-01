package com.tradehero;

import com.ayondo.academy.GooglePlayAppTestModule;
import dagger.Module;

@Module(
        includes = {
                GooglePlayAppTestModule.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class FlavorAppTestModule
{
}
