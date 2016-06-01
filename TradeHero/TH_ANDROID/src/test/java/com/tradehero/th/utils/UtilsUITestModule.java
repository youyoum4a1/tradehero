package com.ayondo.academy.utils;

import com.ayondo.academy.utils.route.UtilsRouteUITestModule;
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
