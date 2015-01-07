package com.tradehero.th.models.social.follower;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.fragments.social.follower.AllFollowerFragment;
import com.tradehero.th.fragments.social.hero.AllHeroFragment;
import com.tradehero.th.persistence.social.HeroType;

public class AllHeroTypeResourceDTO extends HeroTypeResourceDTO
{
    public AllHeroTypeResourceDTO()
    {
        super(
                R.string.leaderboard_community_hero_all,
                0,
                AllHeroFragment.class,

                R.string.leaderboard_community_hero_all,
                0,
                AllFollowerFragment.class);
    }

    @Override @NonNull public HeroType getHeroType()
    {
        return HeroType.ALL;
    }
}
