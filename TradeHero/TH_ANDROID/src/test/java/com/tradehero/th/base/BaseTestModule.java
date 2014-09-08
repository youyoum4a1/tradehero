package com.tradehero.th.base;

import dagger.Module;

@Module(
        injects = {
                VersioningTest.class,
        },
        complete = false,
        library = true
)
public class BaseTestModule
{
}
