package com.tradehero.th.models.social.follower;

import com.tradehero.th.persistence.social.HeroType;
import java.util.ArrayList;
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

    public ArrayList<HeroTypeResourceDTO> getListOfHeroType()
    {
        ArrayList<HeroTypeResourceDTO> list = new ArrayList<>();
        for (HeroType heroType : HeroType.values())
        {
            list.add(create(heroType));
        }
        return list;
    }
}
