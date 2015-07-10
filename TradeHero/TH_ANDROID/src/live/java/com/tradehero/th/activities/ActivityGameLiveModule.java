package com.tradehero.th.activities;

import com.tradehero.th.models.LiveModelsModule;
import dagger.Module;

@Module(
        includes = {
                LiveModelsModule.class,
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
