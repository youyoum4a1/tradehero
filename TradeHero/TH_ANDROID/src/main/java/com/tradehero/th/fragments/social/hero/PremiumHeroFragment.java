package com.tradehero.th.fragments.social.hero;

import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.persistence.social.HeroType;

public class PremiumHeroFragment extends HeroesTabContentFragment
{
    @Override protected HeroType getHeroType()
    {
        return HeroType.PREMIUM;
    }

    @Override protected void display(HeroIdExtWrapper heroIds)
    {
        display(heroCache.get().get(heroIds.activePremiumHeroes));
    }
}