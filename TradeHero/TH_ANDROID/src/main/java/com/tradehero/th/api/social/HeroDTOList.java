package com.tradehero.th.api.social;

import com.android.internal.util.Predicate;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;

public class HeroDTOList extends BaseArrayList<HeroDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public HeroDTOList()
    {
        super();
    }
    //</editor-fold>

    @NotNull public FollowerHeroRelationIdList createKeys(@NotNull UserBaseKey followerId)
    {
        FollowerHeroRelationIdList followerHeroRelationIdList = new FollowerHeroRelationIdList();
        for (@NotNull HeroDTO heroDTO : this)
        {
            followerHeroRelationIdList.add(heroDTO.getHeroId(followerId));
        }
        return followerHeroRelationIdList;
    }

    @NotNull public HeroDTOList filter(@NotNull Predicate<HeroDTO> predicate)
    {
        HeroDTOList filtered = new HeroDTOList();
        for (@NotNull HeroDTO heroDTO : this)
        {
            if (predicate.apply(heroDTO))
            {
                filtered.add(heroDTO);
            }
        }
        return filtered;
    }

    @NotNull public HeroDTOList getAllActiveHeroIds()
    {
        return filter(new HeroDTOActiveFreePredicateImpl(true, null));
    }

    @NotNull public HeroDTOList getFreeActiveHeroIds()
    {
        return filter(new HeroDTOActiveFreePredicateImpl(true, true));
    }

    @NotNull public HeroDTOList getPremiumActiveHeroIds()
    {
        return filter(new HeroDTOActiveFreePredicateImpl(true, false));
    }
}
