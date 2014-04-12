package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.key.HeroId;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 2:35 PM To change this template use File | Settings | File Templates. */
@Singleton public class HeroCache extends StraightDTOCache<HeroId, HeroDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public HeroCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected HeroDTO fetch(HeroId key)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    public HeroDTOList put(UserBaseKey followerId, HeroDTOList values)
    {
        if (values == null)
        {
            return null;
        }

        HeroDTOList previousValues = new HeroDTOList();

        for (HeroDTO value: values)
        {
            previousValues.add(put(value.getHeroId(followerId), value));
        }

        return previousValues;
    }

    public HeroDTOList get(List<HeroId> heroIds)
    {
        if (heroIds == null)
        {
            return null;
        }
        HeroDTOList heroDTOs = new HeroDTOList();

        for (HeroId heroId: heroIds)
        {
            heroDTOs.add(get(heroId));
        }

        return heroDTOs;
    }
}
