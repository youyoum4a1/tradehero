package com.tradehero.th.utils;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                ColorUtilsTest.class,
                NumberDisplayUtilsTest.class,
        },
        complete = false,
        library = true
)public class AppUtilsTestModule
{
}
