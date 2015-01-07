package com.tradehero.th.models.social.follower;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.fragments.social.follower.PremiumFollowerFragment;
import com.tradehero.th.fragments.social.hero.PremiumHeroFragment;
import com.tradehero.th.persistence.social.HeroType;

public class PremiumHeroTypeResourceDTO extends HeroTypeResourceDTO
{
    public PremiumHeroTypeResourceDTO()
    {
        super(
                R.string.leaderboard_community_hero_premium,
                2,
                PremiumHeroFragment.class,

                R.string.leaderboard_community_hero_premium,
                2,
                PremiumFollowerFragment.class);
    }

    @Override @NonNull public HeroType getHeroType()
    {
        return HeroType.PREMIUM;
    }
}
