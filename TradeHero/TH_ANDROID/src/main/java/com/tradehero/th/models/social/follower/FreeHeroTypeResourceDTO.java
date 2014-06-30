package com.tradehero.th.models.social.follower;

import com.tradehero.thm.R;
import com.tradehero.th.fragments.social.follower.FreeFollowerFragment;
import com.tradehero.th.fragments.social.hero.FreeHeroFragment;
import com.tradehero.th.persistence.social.HeroType;

public class FreeHeroTypeResourceDTO extends HeroTypeResourceDTO
{
    public FreeHeroTypeResourceDTO()
    {
        super(
                R.string.leaderboard_community_hero_free,
                1,
                FreeHeroFragment.class,

                R.string.leaderboard_community_hero_free,
                1,
                FreeFollowerFragment.class);
    }

    @Override public HeroType getHeroType()
    {
        return HeroType.FREE;
    }
}
