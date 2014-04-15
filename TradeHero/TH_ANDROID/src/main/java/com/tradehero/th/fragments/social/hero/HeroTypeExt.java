package com.tradehero.th.fragments.social.hero;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.persistence.social.HeroType;

/**
 * Created by thonguyen on 3/4/14.
 */
public class HeroTypeExt
{
    public final int titleRes;
    public final HeroType heroType;
    public final int pageIndex;
    public final Class<? extends Fragment> fragmentClass;

    public HeroTypeExt(int titleRes, HeroType followerType, int pageIndex,Class<? extends Fragment> fragmentClass)
    {
        this.titleRes = titleRes;
        this.heroType = followerType;
        this.pageIndex = pageIndex;
        this.fragmentClass = fragmentClass;
    }

    public static HeroTypeExt[] getSortedList()
    {
        HeroType[] arr = HeroType.values();
        int len = arr.length;
        HeroTypeExt[] result = new HeroTypeExt[arr.length];

        for (int i = 0; i < len; i++)
        {
            int typeId = arr[i].typeId;
            if (typeId == HeroType.PREMIUM.typeId)
            {
                result[i] = new HeroTypeExt(R.string.leaderboard_community_hero_premium,
                        HeroType.PREMIUM, 0, PrimiumHeroFragment.class);
            }
            else if (typeId == HeroType.FREE.typeId)
            {
                result[i] = new HeroTypeExt(R.string.leaderboard_community_hero_free, HeroType.FREE, 1,
                        FreeHeroFragment.class);
            }
            else if (typeId == HeroType.ALL.typeId)
            {
                result[i] = new HeroTypeExt(R.string.leaderboard_community_hero_all, HeroType.ALL, 2,
                        AllHeroFragment.class);
            }
        }
        return result;
    }

    public static HeroTypeExt fromIndex(HeroTypeExt[] arr, int pageIndex)
    {
        for (HeroTypeExt type : arr)
        {
            if (type.pageIndex == pageIndex)
            {
                return type;
            }
        }
        return null;
    }
}