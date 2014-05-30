package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class HeroCache extends StraightDTOCacheNew<FollowerHeroRelationId, HeroDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public HeroCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override public HeroDTO fetch(FollowerHeroRelationId key)
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

    public HeroDTOList get(List<FollowerHeroRelationId> heroIds)
    {
        if (heroIds == null)
        {
            return null;
        }
        HeroDTOList heroDTOs = new HeroDTOList();

        for (FollowerHeroRelationId heroId: heroIds)
        {
            heroDTOs.add(get(heroId));
        }

        return heroDTOs;
    }
}
