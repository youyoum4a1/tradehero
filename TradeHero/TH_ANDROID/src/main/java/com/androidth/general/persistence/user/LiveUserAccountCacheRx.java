package com.androidth.general.persistence.user;

import android.support.annotation.NonNull;

import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserLiveAccount;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.network.service.Live1BServiceWrapper;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.persistence.leaderboard.LeaderboardCacheRx;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;

@Singleton @UserCache
public class LiveUserAccountCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, UserLiveAccount>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final Lazy<Live1BServiceWrapper> liveServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LiveUserAccountCacheRx(
            @NonNull Lazy<Live1BServiceWrapper> liveServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.liveServiceWrapper = liveServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserLiveAccount> fetch(@NonNull UserBaseKey key)
    {
        return liveServiceWrapper.get().getUserLiveAccount();
    }

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull UserLiveAccount userLiveAccount)
    {
        super.onNext(key, userLiveAccount);
    }

}
