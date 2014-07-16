package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class HeroCache extends StraightDTOCacheNew<FollowerHeroRelationId, HeroDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public HeroCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override @NotNull public HeroDTO fetch(@NotNull FollowerHeroRelationId key)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Contract("_, null -> null; _, !null -> !null") @Nullable
    public HeroDTOList put(@NotNull UserBaseKey followerId, @Nullable HeroDTOList values)
    {
        if (values == null)
        {
            return null;
        }

        HeroDTOList previousValues = new HeroDTOList();
        for (@NotNull HeroDTO value: values)
        {
            previousValues.add(put(value.getHeroId(followerId), value));
        }
        return previousValues;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public HeroDTOList get(@Nullable List<FollowerHeroRelationId> heroIds)
    {
        if (heroIds == null)
        {
            return null;
        }

        HeroDTOList heroDTOs = new HeroDTOList();
        for (@NotNull FollowerHeroRelationId followerHeroRelationId: heroIds)
        {
            heroDTOs.add(get(followerHeroRelationId));
        }
        return heroDTOs;
    }
}
