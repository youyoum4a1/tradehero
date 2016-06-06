package com.androidth.general.api;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
        },
        complete = false,
        library = true,
        overrides = true
)
public class SamsungApiTestModule
{
    @Provides ValidMocker provideValidMocker(ValidMockerSamsung validMocker)
    {
        return validMocker;
    }
}
