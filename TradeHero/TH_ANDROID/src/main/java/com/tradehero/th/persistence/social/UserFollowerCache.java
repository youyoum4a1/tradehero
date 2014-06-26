package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class UserFollowerCache extends StraightDTOCacheNew<FollowerHeroRelationId, UserFollowerDTO>
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

    @Override public UserFollowerDTO fetch(@NotNull FollowerHeroRelationId key) throws Throwable
    {
        return this.followerServiceWrapper.get().getFollowerSubscriptionDetail(key);
    }
}
