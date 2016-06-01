package com.ayondo.academy.activities;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                SignUpLiveActivity.class,
                IdentityPromptActivity.class
        },
        complete = false,
        library = true
)
public class ActivityGameLiveModule
{
}
