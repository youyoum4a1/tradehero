package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache
public class UserFollowerCache extends StraightDTOCacheNew<FollowerHeroRelationId, UserFollowerDTO>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    @NotNull private final Lazy<FollowerServiceWrapper> followerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserFollowerCache(
            @NotNull Lazy<FollowerServiceWrapper> followerServiceWrapper,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.followerServiceWrapper = followerServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull public UserFollowerDTO fetch(@NotNull FollowerHeroRelationId key) throws Throwable
    {
        return this.followerServiceWrapper.get().getFollowerSubscriptionDetail(key);
    }
}
