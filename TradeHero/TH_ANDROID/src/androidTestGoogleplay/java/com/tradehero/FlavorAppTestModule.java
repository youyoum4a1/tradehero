package com.tradehero;

import com.tradehero.th.GooglePlayAppTestModule;
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
