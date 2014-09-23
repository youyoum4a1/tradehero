package com.tradehero.th.utils;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                THColorUtilsTest.class,
                NumberDisplayUtilsTest.class,
        },
        complete = false,
        library = true
)public class UtilsTestModule
{
}
