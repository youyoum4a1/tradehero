package com.tradehero.th.api.social;

import com.tradehero.th.api.users.UserBaseKey;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeroDTOList extends ArrayList<HeroDTO>
{
    //<editor-fold desc="Constructors">
    public HeroDTOList()
    {
        super();
    }
    //</editor-fold>

    @NotNull public HeroIdList filter(
            @NotNull UserBaseKey followerId,
            @NotNull HeroDTOActiveFreePredicate predicate)
    {
        HeroIdList filtered = new HeroIdList();
        for (@Nullable HeroDTO heroDTO : this)
        {
            if (predicate.apply(heroDTO))
            {
                filtered.add(heroDTO.getHeroId(followerId));
            }
        }
        return filtered;
    }

    public HeroIdList getAllActiveHeroIds(@NotNull UserBaseKey followerId)
    {
        return filter(followerId, new HeroDTOActiveFreePredicateImpl(true, null));
    }

    public HeroIdList getFreeActiveHeroIds(@NotNull UserBaseKey followerId)
    {
        return filter(followerId, new HeroDTOActiveFreePredicateImpl(true, true));
    }

    public HeroIdList getPremiumActiveHeroIds(@NotNull UserBaseKey followerId)
    {
        return filter(followerId, new HeroDTOActiveFreePredicateImpl(true, false));
    }
}
