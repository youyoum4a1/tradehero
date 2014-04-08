package com.tradehero.th.api.social;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 12:29 PM To change this template use File | Settings | File Templates. */
public class HeroIdExt extends HeroId
{
    public boolean getPaied;
    public HeroIdExt(Integer heroId, Integer followerId)
    {
        super(heroId, followerId);
    }

    public HeroIdExt(HeroId heroId)
    {
        super(heroId.heroId, heroId.followerId);
    }
}
