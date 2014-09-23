package com.tradehero.th.utils;

import com.tradehero.th.utils.route.UtilsRouteUITestModule;
import dagger.Module;

@Module(
        includes = {
                UtilsRouteUITestModule.class,
        },
        injects = {
        },
        complete = false,
        library = true
)public class UtilsUITestModule
{
}
