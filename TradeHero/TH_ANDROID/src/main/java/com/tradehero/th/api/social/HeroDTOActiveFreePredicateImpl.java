package com.tradehero.th.api.social;

import android.support.annotation.Nullable;

public class HeroDTOActiveFreePredicateImpl extends HeroDTOActiveFreePredicate
{
    @Nullable private final Boolean active;
    @Nullable private final Boolean freeFollow;

    //<editor-fold desc="Constructors">
    /**
     * A null parameter means that it is not tested.
     * @param active
     * @param freeFollow
     */
    public HeroDTOActiveFreePredicateImpl(@Nullable Boolean active, @Nullable Boolean freeFollow)
    {
        super();
        this.active = active;
        this.freeFollow = freeFollow;
    }
    //</editor-fold>

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
