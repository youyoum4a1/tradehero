package com.tradehero.th.activities;

import com.tradehero.th.fragments.CallToActionFragment;
import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                SignUpLiveActivity.class,
                IdentityPromptActivity.class,
                CallToActionFragment.class,
                ConnectAccountActivity.class,
                LiveLoginActivity.class,
                LiveAccountSettingActivity.class
        },
        complete = false,
        library = true
)
public class ActivityGameLiveModule
{
}
