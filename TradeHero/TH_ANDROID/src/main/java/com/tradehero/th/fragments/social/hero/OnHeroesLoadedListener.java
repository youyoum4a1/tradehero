package com.tradehero.th.fragments.social.hero;

import com.tradehero.th.api.social.HeroIdExtWrapper;

public interface OnHeroesLoadedListener
{
    void onHerosLoaded(int page, HeroIdExtWrapper value);
}