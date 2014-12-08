package com.tradehero.th.fragments.social.hero;

import dagger.Component;

@Component
public interface FragmentSocialHeroComponent
{
    void injectHeroManagerFragment(HeroManagerFragment target);
    void injectHeroListItemView(HeroListItemView target);
    void injectHeroesTabContentFragment(HeroesTabContentFragment target);
    void injectPremiumHeroFragment(PremiumHeroFragment target);
    void injectFreeHeroFragment(FreeHeroFragment target);
    void injectAllHeroFragment(AllHeroFragment target);
}
