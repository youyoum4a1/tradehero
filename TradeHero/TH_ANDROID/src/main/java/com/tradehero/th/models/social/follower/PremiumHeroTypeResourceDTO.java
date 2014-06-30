package com.tradehero.th.models.social.follower;

import com.tradehero.thm.R;
import com.tradehero.th.fragments.social.follower.PremiumFollowerFragment;
import com.tradehero.th.fragments.social.hero.PremiumHeroFragment;
import com.tradehero.th.persistence.social.HeroType;

public class PremiumHeroTypeResourceDTO extends HeroTypeResourceDTO
{
    public PremiumHeroTypeResourceDTO()
    {
        super(
                R.string.leaderboard_community_hero_premium,
                0,
                PremiumHeroFragment.class,

                R.string.leaderboard_community_hero_premium,
                0,
                PremiumFollowerFragment.class);
    }

    @Override public HeroType getHeroType()
    {
        return HeroType.PREMIUM;
    }
}
