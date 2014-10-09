package com.tradehero.th.fragments.social.hero;

import android.content.Context;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.persistence.social.HeroType;
import javax.inject.Inject;

public class FreeHeroFragment extends HeroesTabContentFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override protected HeroType getHeroType()
    {
        return HeroType.FREE;
    }

    @Override protected void display(HeroDTOExtWrapper heroDTOExtWrapper)
    {
        display(heroDTOExtWrapper.activeFreeHeroes);
    }
}