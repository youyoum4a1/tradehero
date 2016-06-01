package com.tradehero;

import com.ayondo.academy.AmazonAppTestModule;
import dagger.Module;

@Module(
        includes = {
                AmazonAppTestModule.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class FlavorAppTestModule
{
}
