package com.tradehero.th.activities;

import dagger.Module;

@Module(
        injects = {
                LiveActivityUtil.class,
                SignUpLiveActivity.class
        },
        complete = false,
        library = true
)
public class ActivityGameLiveModule
{
}
