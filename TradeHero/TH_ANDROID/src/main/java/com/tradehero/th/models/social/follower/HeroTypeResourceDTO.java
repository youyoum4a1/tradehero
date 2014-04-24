package com.tradehero.th.models.social.follower;

import android.support.v4.app.Fragment;
import com.tradehero.th.persistence.social.HeroType;

abstract public class HeroTypeResourceDTO
{
    public final int titleRes;
    public final int pageIndex;
    public final Class<? extends Fragment> fragmentClass;

    protected HeroTypeResourceDTO(
            int titleRes,
            int pageIndex,
            Class<? extends Fragment> fragmentClass)
    {
        this.titleRes = titleRes;
        this.pageIndex = pageIndex;
        this.fragmentClass = fragmentClass;
    }

    abstract public HeroType getFollowerType();
}
