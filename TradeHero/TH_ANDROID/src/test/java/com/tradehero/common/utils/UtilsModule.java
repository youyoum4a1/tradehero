package com.tradehero.common.utils;

import dagger.Module;

@Module(
        injects = {
                ConverterDeserialisationTest.class,
                ConverterSerialisationTest.class
        },
        complete = false,
        library = true
)
public class UtilsModule
{
}
