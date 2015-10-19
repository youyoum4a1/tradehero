package com.tradehero.th.activities;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                SignUpLiveActivity.class,
                IdentityPromptActivity.class,
                CallToActionActivity.class,
                ConnectAccountActivity.class,
                LiveLoginActivity.class,
        },
        complete = false,
        library = true
)
public class ActivityGameLiveModule
{
}
