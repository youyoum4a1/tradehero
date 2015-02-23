package com.tradehero.th.fragments.onboarding.hero;

import dagger.Module;

@Module(
        injects = {
                SelectableUserViewRelative.class,
                OnBoardUserItemView.class,
                UserSelectionScreenFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentOnBoardHeroModule
{
}
