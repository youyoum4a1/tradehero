package com.tradehero.th.models.social.follower;

import com.tradehero.th.R;
import com.tradehero.th.fragments.social.follower.PremiumFollowerFragment;
import com.tradehero.th.persistence.social.HeroType;

public class PremiumHeroTypeResourceDTO extends HeroTypeResourceDTO
{
    public PremiumHeroTypeResourceDTO()
    {
        super(
                R.string.leaderboard_community_hero_premium,
                0,
                PremiumFollowerFragment.class);
    }

    @Override public HeroType getFollowerType()
    {
        return HeroType.PREMIUM;
    }
}
