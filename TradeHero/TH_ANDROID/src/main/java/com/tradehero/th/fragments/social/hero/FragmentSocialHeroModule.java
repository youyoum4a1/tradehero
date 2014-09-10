package com.tradehero.th.fragments.social.hero;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                HeroManagerInfoFetcher.class,
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
