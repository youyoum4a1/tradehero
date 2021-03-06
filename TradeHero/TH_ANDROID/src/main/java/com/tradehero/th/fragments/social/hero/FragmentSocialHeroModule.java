package com.tradehero.th.fragments.social.hero;

import dagger.Module;

@Module(
        injects = {
                HeroManagerFragment.class,
                HeroListItemView.class,
                HeroesTabContentFragment.class,
                PremiumHeroFragment.class,
                FreeHeroFragment.class,
                AllHeroFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentSocialHeroModule
{
}
