package com.tradehero.th.models.social.follower;

import com.tradehero.th.R;
import com.tradehero.th.fragments.social.follower.AllFollowerFragment;
import com.tradehero.th.persistence.social.HeroType;

public class AllHeroTypeResourceDTO extends HeroTypeResourceDTO
{
    public AllHeroTypeResourceDTO()
    {
        super(
                R.string.leaderboard_community_hero_all,
                2,
                AllFollowerFragment.class);
    }

    @Override public HeroType getFollowerType()
    {
        return HeroType.ALL;
    }
}
