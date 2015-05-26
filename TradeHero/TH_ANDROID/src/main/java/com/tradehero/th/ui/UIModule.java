package com.tradehero.th.ui;

import org.ocpsoft.prettytime.PrettyTime;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
        },
        injects = {
        },
        complete = false,
        library = true
)
public class UIModule
{
    @Provides PrettyTime providePrettyTime()
    {
        return new PrettyTime();
    }
}
