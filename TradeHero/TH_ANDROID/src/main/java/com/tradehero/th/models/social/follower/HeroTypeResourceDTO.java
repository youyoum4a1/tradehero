package com.tradehero.th.models.social.follower;

import android.support.v4.app.Fragment;
import com.tradehero.th.persistence.social.HeroType;

abstract public class HeroTypeResourceDTO
{
    public final int heroTabTitleRes;
    public final int heroTabIndex;
    public final Class<? extends Fragment> heroContentFragmentClass;

    public final int followerTabTitleRes;
    public final int followerTabIndex;
    public final Class<? extends Fragment> followerContentFragmentClass;

    protected HeroTypeResourceDTO(
            int heroTabTitleRes,
            int heroTabIndex,
            Class<? extends Fragment> heroContentFragmentClass,

            int followerTabTitleRes,
            int followerTabIndex,
            Class<? extends Fragment> followerContentFragmentClass)
    {
        this.heroTabTitleRes = heroTabTitleRes;
        this.heroTabIndex = heroTabIndex;
        this.heroContentFragmentClass = heroContentFragmentClass;

        this.followerTabTitleRes = followerTabTitleRes;
        this.followerTabIndex = followerTabIndex;
        this.followerContentFragmentClass = followerContentFragmentClass;
    }

    abstract public HeroType getHeroType();
}
