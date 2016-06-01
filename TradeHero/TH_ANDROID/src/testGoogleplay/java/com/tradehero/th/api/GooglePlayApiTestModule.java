package com.ayondo.academy.api;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
        },
        complete = false,
        library = true,
        overrides = true
)
public class GooglePlayApiTestModule
{
    @Provides ValidMocker provideValidMocker(ValidMockerGooglePlay validMocker)
    {
        return validMocker;
    }
}
