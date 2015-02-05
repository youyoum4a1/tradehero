package com.tradehero.th.models.social.follower;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

abstract public class HeroTypeResourceDTO
{
    @StringRes public final int heroTabTitleRes;
    public final int heroTabIndex;
    @NonNull public final Class<? extends Fragment> heroContentFragmentClass;

    @StringRes public final int followerTabTitleRes;
    public final int followerTabIndex;
    @NonNull public final Class<? extends Fragment> followerContentFragmentClass;

    //<editor-fold desc="Constructors">
    protected HeroTypeResourceDTO(
            @StringRes int heroTabTitleRes,
            int heroTabIndex,
            @NonNull Class<? extends Fragment> heroContentFragmentClass,

            @StringRes int followerTabTitleRes,
            int followerTabIndex,
            @NonNull Class<? extends Fragment> followerContentFragmentClass)
    {
        this.heroTabTitleRes = heroTabTitleRes;
        this.heroTabIndex = heroTabIndex;
        this.heroContentFragmentClass = heroContentFragmentClass;

        this.followerTabTitleRes = followerTabTitleRes;
        this.followerTabIndex = followerTabIndex;
        this.followerContentFragmentClass = followerContentFragmentClass;
    }
    //</editor-fold>
}
