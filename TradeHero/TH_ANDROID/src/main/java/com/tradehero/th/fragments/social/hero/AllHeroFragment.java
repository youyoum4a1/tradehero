package com.tradehero.th.fragments.social.hero;

import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.persistence.social.HeroType;

public class AllHeroFragment extends HeroesTabContentFragment
{
    @Override protected HeroType getHeroType()
    {
        return HeroType.ALL;
    }

    @Override protected void display(HeroDTOExtWrapper heroDTOExtWrapper)
    {
        display(heroDTOExtWrapper.allActiveHeroes);
    }
}