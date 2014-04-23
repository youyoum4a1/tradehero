package com.tradehero.th.api.social;

import com.android.internal.util.Predicate;

public class HeroDTOActiveFreePredicate implements Predicate<HeroDTO>
{
    private final Boolean active;
    private final Boolean freeFollow;

    /**
     * A null parameter means that it is not tested.
     * @param active
     * @param freeFollow
     */
    public HeroDTOActiveFreePredicate(Boolean active, Boolean freeFollow)
    {
        super();
        this.active = active;
        this.freeFollow = freeFollow;
    }

    @Override public boolean apply(HeroDTO heroDTO)
    {
        if (heroDTO == null)
        {
            return false;
        }
        return (active == null || (heroDTO.active == active)) &&
                (freeFollow == null || (heroDTO.isFreeFollow == freeFollow));
    }
}
