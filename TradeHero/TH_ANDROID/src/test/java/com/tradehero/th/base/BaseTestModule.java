package com.ayondo.academy.base;

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
