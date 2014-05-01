package com.tradehero.th.fragments.social.hero;

import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;

public interface OnHeroesLoadedListener
{
    void onHerosLoaded(HeroTypeResourceDTO resourceDTO, HeroIdExtWrapper value);
}