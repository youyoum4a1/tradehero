package com.tradehero.th.models.social.follower;

import com.tradehero.th.R;
import com.tradehero.th.fragments.social.follower.FollowersFragment;
import com.tradehero.th.fragments.social.hero.AllHeroFragment;
import com.tradehero.th.persistence.social.HeroType;

public class AllHeroTypeResourceDTO extends HeroTypeResourceDTO
{
    //<editor-fold desc="Constructors">
    public AllHeroTypeResourceDTO()
    {
        super(
                R.string.leaderboard_community_hero_all,
                HeroType.ALL.ordinal(),
                AllHeroFragment.class,

                R.string.leaderboard_community_hero_all,
                HeroType.ALL.ordinal(),
                FollowersFragment.class);
    }
    //</editor-fold>
}
