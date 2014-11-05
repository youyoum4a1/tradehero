package com.tradehero.th.api.social;

import com.android.internal.util.Predicate;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.users.UserBaseKey;
import android.support.annotation.NonNull;

public class HeroDTOList extends BaseArrayList<HeroDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public HeroDTOList()
    {
        super();
    }
    //</editor-fold>

    @NonNull public FollowerHeroRelationIdList createKeys(@NonNull UserBaseKey followerId)
    {
        FollowerHeroRelationIdList followerHeroRelationIdList = new FollowerHeroRelationIdList();
        for (HeroDTO heroDTO : this)
        {
            followerHeroRelationIdList.add(heroDTO.getHeroId(followerId));
        }
        return followerHeroRelationIdList;
    }

    @NonNull public HeroDTOList filter(@NonNull Predicate<HeroDTO> predicate)
    {
        HeroDTOList filtered = new HeroDTOList();
        for (HeroDTO heroDTO : this)
        {
            if (predicate.apply(heroDTO))
            {
                filtered.add(heroDTO);
            }
        }
        return filtered;
    }

    @NonNull public HeroDTOList getAllActiveHeroIds()
    {
        return filter(new HeroDTOActiveFreePredicateImpl(true, null));
    }

    @NonNull public HeroDTOList getFreeActiveHeroIds()
    {
        return filter(new HeroDTOActiveFreePredicateImpl(true, true));
    }

    @NonNull public HeroDTOList getPremiumActiveHeroIds()
    {
        return filter(new HeroDTOActiveFreePredicateImpl(true, false));
    }
}
