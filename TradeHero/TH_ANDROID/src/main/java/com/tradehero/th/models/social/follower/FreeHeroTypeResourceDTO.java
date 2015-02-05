package com.tradehero.th.models.social.follower;

import com.tradehero.th.R;
import com.tradehero.th.fragments.social.follower.FreeFollowerFragment;
import com.tradehero.th.fragments.social.hero.FreeHeroFragment;
import com.tradehero.th.persistence.social.HeroType;

public class FreeHeroTypeResourceDTO extends HeroTypeResourceDTO
{
    //<editor-fold desc="Constructors">
    public FreeHeroTypeResourceDTO()
    {
        super(
                R.string.leaderboard_community_hero_free,
                HeroType.FREE.ordinal(),
                FreeHeroFragment.class,

                R.string.leaderboard_community_hero_free,
                HeroType.FREE.ordinal(),
                FreeFollowerFragment.class);
    }
    //</editor-fold>
}
