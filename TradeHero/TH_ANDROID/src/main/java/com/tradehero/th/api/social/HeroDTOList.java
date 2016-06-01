package com.ayondo.academy.api.social;

import android.support.annotation.NonNull;
import com.android.internal.util.Predicate;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;

public class HeroDTOList extends BaseArrayList<HeroDTO>
    implements DTO
{
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
