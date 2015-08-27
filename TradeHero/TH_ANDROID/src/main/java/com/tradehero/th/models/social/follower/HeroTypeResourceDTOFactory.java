package com.tradehero.th.models.social.follower;

import android.support.annotation.NonNull;
import com.tradehero.th.persistence.social.HeroType;
import java.util.ArrayList;

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

    @NonNull public static ArrayList<HeroTypeResourceDTO> getListOfHeroType()
    {
        ArrayList<HeroTypeResourceDTO> list = new ArrayList<>();
        for (HeroType heroType : HeroType.values())
        {
            list.add(create(heroType));
        }
        return list;
    }
}
