package com.tradehero.th.fragments.social.hero;

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

    public HeroTypeExt(int titleRes, HeroType followerType, int pageIndex)
    {
        this.titleRes = titleRes;
        this.heroType = followerType;
        this.pageIndex = pageIndex;
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
                        HeroType.PREMIUM, 0);
            }
            else if (typeId == HeroType.FREE.typeId)
            {
                result[i] = new HeroTypeExt(R.string.leaderboard_community_hero_free, HeroType.FREE, 1);
            }
            else if (typeId == HeroType.ALL.typeId)
            {
                result[i] = new HeroTypeExt(R.string.leaderboard_community_hero_all, HeroType.ALL, 2);
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