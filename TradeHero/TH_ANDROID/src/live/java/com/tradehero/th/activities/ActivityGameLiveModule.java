package com.tradehero.th.activities;

import com.tradehero.th.models.fastfill.FastFillModule;
import dagger.Module;

@Module(
        includes = {
                FastFillModule.class,
        },
        injects = {
                LiveActivityUtil.class,
                SignUpLiveActivity.class,
                IdentityPromptActivity.class
        },
        complete = false,
        library = true
)
public class ActivityGameLiveModule
{
}
