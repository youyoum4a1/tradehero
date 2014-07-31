package com.tradehero.th.fragments.settings;

import dagger.Module;

@Module(
        injects = {
                FriendListAdapterTest.class,
                UserTranslationSettingsViewHolderTest.class,
        },
        complete = false,
        library = true
)
public class FragmentSettingsTestModule
{
}
