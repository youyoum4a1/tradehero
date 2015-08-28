package com.tradehero.th.models.social.follower;

import android.support.annotation.NonNull;
import com.tradehero.th.persistence.social.HeroType;

public class HeroTypeResourceDTOFactory
{
    @NonNull public static HeroTypeResourceDTO create(@NonNull HeroType heroType)
    {
        switch (heroType)
        {
            case ALL:
                return new AllHeroTypeResourceDTO();
            default:
                throw new IllegalArgumentException("Unhandled HeroType " + heroType);
        }
    }
}
