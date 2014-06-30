package com.tradehero.th.models.social.follower;

import com.tradehero.thm.R;
import com.tradehero.th.fragments.social.follower.AllFollowerFragment;
import com.tradehero.th.fragments.social.hero.AllHeroFragment;
import com.tradehero.th.persistence.social.HeroType;

public class AllHeroTypeResourceDTO extends HeroTypeResourceDTO
{
    public AllHeroTypeResourceDTO()
    {
        super(
                R.string.leaderboard_community_hero_all,
                2,
                AllHeroFragment.class,

                R.string.leaderboard_community_hero_all,
                2,
                AllFollowerFragment.class);
    }

    @Override public HeroType getHeroType()
    {
        return HeroType.ALL;
    }
}
