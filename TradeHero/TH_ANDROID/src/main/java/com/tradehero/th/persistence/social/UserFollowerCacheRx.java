package com.ayondo.academy.persistence.social;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.social.UserFollowerDTO;
import com.ayondo.academy.api.social.key.FollowerHeroRelationId;
import com.ayondo.academy.network.service.FollowerServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class UserFollowerCacheRx extends BaseFetchDTOCacheRx<FollowerHeroRelationId, UserFollowerDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;

    @NonNull private final Lazy<FollowerServiceWrapper> followerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserFollowerCacheRx(
            @NonNull Lazy<FollowerServiceWrapper> followerServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.followerServiceWrapper = followerServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserFollowerDTO> fetch(@NonNull FollowerHeroRelationId key)
    {
        return this.followerServiceWrapper.get().getFollowerSubscriptionDetailRx(key);
    }
}
