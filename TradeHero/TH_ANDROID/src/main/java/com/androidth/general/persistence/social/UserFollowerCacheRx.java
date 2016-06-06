package com.androidth.general.persistence.social;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.social.UserFollowerDTO;
import com.androidth.general.api.social.key.FollowerHeroRelationId;
import com.androidth.general.network.service.FollowerServiceWrapper;
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
