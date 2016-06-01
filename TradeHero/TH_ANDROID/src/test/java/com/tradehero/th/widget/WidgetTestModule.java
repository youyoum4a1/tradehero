package com.ayondo.academy.widget;

import dagger.Module;

@Module(
        injects = {
                UserLevelProgressBarTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class WidgetTestModule
{
}
