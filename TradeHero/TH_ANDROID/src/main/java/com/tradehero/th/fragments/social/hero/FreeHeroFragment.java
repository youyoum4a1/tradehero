package com.tradehero.th.fragments.social.hero;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.persistence.social.HeroType;
import java.util.List;
import javax.inject.Inject;

public class FreeHeroFragment extends HeroesTabContentFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @NonNull @Override protected HeroType getHeroType()
    {
        return HeroType.FREE;
    }

    @Override protected List<HeroDTO> getHeroes(@NonNull HeroDTOExtWrapper heroDTOExtWrapper)
    {
        return heroDTOExtWrapper.activeFreeHeroes;
    }
}