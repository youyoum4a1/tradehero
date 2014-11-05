package com.tradehero.th.api.social;

import android.support.annotation.Nullable;

public class HeroDTOActiveFreePredicateImpl extends HeroDTOActiveFreePredicate
{
    private final Boolean active;
    private final Boolean freeFollow;

    /**
     * A null parameter means that it is not tested.
     * @param active
     * @param freeFollow
     */
    public HeroDTOActiveFreePredicateImpl(Boolean active, Boolean freeFollow)
    {
        super();
        this.active = active;
        this.freeFollow = freeFollow;
    }

    @Override public boolean apply(@Nullable HeroDTO heroDTO)
    {
        if (heroDTO == null)
        {
            return false;
        }
        return (active == null || (heroDTO.active == active)) &&
                (freeFollow == null || (heroDTO.isFreeFollow == freeFollow));
    }
}
