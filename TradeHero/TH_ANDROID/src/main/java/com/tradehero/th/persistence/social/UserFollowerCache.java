package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserFollowerCache extends StraightDTOCache<FollowerHeroRelationId, UserFollowerDTO>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    @NotNull private final Lazy<FollowerServiceWrapper> followerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserFollowerCache(@NotNull Lazy<FollowerServiceWrapper> followerServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.followerServiceWrapper = followerServiceWrapper;
    }
    //</editor-fold>

    @Override protected UserFollowerDTO fetch(@NotNull FollowerHeroRelationId key) throws Throwable
    {
        return this.followerServiceWrapper.get().getFollowerSubscriptionDetail(key);
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<UserFollowerDTO> getOrFetch(@Nullable List<FollowerHeroRelationId> followerHeroRelationIds) throws Throwable
    {
        if (followerHeroRelationIds == null)
        {
            return null;
        }

        List<UserFollowerDTO> followerSummaryDTOs = new ArrayList<>();
        for (@NotNull FollowerHeroRelationId baseKey: followerHeroRelationIds)
        {
            followerSummaryDTOs.add(getOrFetch(baseKey, false));
        }
        return followerSummaryDTOs;
    }
}
