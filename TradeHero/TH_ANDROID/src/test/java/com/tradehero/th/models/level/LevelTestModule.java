package com.tradehero.th.models.level;

import dagger.Module;

@Module(
        injects = {
                LevelDefUtilTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class LevelTestModule
{
}
