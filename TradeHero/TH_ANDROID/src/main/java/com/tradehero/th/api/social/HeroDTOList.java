package com.tradehero.th.api.social;

import com.android.internal.util.Predicate;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.ArrayList;
import java.util.Collection;

public class HeroDTOList extends ArrayList<HeroDTO>
{
    //<editor-fold desc="Constructors">
    public HeroDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public HeroDTOList()
    {
        super();
    }

    public HeroDTOList(Collection<? extends HeroDTO> c)
    {
        super(c);
    }
    //</editor-fold>

    public HeroIdList filter(UserBaseKey followerId, Predicate<HeroDTO> predicate)
    {
        HeroIdList filtered = new HeroIdList();
        for (HeroDTO heroDTO : this)
        {
            if (predicate.apply(heroDTO))
            {
                filtered.add(heroDTO.getHeroId(followerId));
            }
        }
        return filtered;
    }

    public HeroIdList getAllActiveHeroIds(UserBaseKey followerId)
    {
        return filter(followerId, new HeroDTOActiveFreePredicate(true, null));
    }

    public HeroIdList getFreeActiveHeroIds(UserBaseKey followerId)
    {
        return filter(followerId, new HeroDTOActiveFreePredicate(true, true));
    }

    public HeroIdList getPremiumActiveHeroIds(UserBaseKey followerId)
    {
        return filter(followerId, new HeroDTOActiveFreePredicate(true, false));
    }
}
