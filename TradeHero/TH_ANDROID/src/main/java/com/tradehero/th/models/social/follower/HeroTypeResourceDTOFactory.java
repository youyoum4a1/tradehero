package com.tradehero.th.models.social.follower;

import com.tradehero.th.persistence.social.HeroType;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Inject;

public class HeroTypeResourceDTOFactory
{
    @Inject public HeroTypeResourceDTOFactory()
    {
        super();
    }

    public HeroTypeResourceDTO create(HeroType heroType)
    {
        switch (heroType)
        {
            case ALL:
                return new AllHeroTypeResourceDTO();
            case FREE:
                return new FreeHeroTypeResourceDTO();
            case PREMIUM:
                return new PremiumHeroTypeResourceDTO();
            default:
                throw new IllegalArgumentException("Unhandled HeroType " + heroType);
        }
    }

    public Map<Integer, HeroTypeResourceDTO> getMapByHeroTypeId()
    {
        //The order of HashMap is unsure,so use TreeMap
        TreeMap<Integer, HeroTypeResourceDTO> map = new TreeMap<>();
        for (HeroType heroType : HeroType.values())
        {
            map.put(heroType.typeId, create(heroType));
        }
        return map;
    }
}
