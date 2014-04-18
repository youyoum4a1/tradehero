package com.tradehero.th.api.social;

import com.tradehero.th.api.social.key.FollowerHeroRelationId;

public class HeroIdExt extends FollowerHeroRelationId
{
    public boolean getPaid;

    public HeroIdExt(Integer heroId, Integer followerId)
    {
        super(heroId, followerId);
    }

    public HeroIdExt(FollowerHeroRelationId heroId)
    {
        super(heroId.heroId, heroId.followerId);
    }

    @Override public String toString()
    {
        return "HeroIdExt{" +
                "getPaid=" + getPaid +" heroId "+heroId+
                '}';
    }
}
