package com.tradehero.th.utils;

import com.tradehero.th.utils.route.AppUtilsRouteTestModule;
import dagger.Module;

@Module(
        includes = {
                AppUtilsRouteTestModule.class,
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
