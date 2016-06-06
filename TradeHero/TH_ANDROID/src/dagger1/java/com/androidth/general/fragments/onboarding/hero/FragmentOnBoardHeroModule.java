package com.androidth.general.fragments.onboarding.hero;

import dagger.Module;

@Module(
        injects = {
                OnBoardUserItemView.class,
                UserSelectionScreenFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentOnBoardHeroModule
{
}
