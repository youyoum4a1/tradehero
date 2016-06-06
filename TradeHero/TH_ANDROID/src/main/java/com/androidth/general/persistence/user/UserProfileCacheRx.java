package com.androidth.general.persistence.user;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.persistence.leaderboard.LeaderboardCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class UserProfileCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, UserProfileDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final Lazy<UserServiceWrapper> userServiceWrapper;
    @NonNull private final Lazy<UserProfileCompactCacheRx> userProfileCompactCache;
    @NonNull private final Lazy<LeaderboardCacheRx> leaderboardCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCacheRx(
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<UserProfileCompactCacheRx> userProfileCompactCache,
            @NonNull Lazy<LeaderboardCacheRx> leaderboardCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.userProfileCompactCache = userProfileCompactCache;
        this.leaderboardCache = leaderboardCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserProfileDTO> fetch(@NonNull UserBaseKey key)
    {
        return userServiceWrapper.get().getUserRx(key);
    }

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull UserProfileDTO userProfileDTO)
    {
        if (userProfileDTO.mostSkilledLbmu != null)
        {
            leaderboardCache.get().onNext(userProfileDTO.getMostSkilledUserOnLbmuKey(), userProfileDTO.mostSkilledLbmu);
        }
        userProfileCompactCache.get().onNext(key, userProfileDTO);
        super.onNext(key, userProfileDTO);
    }

    public void updateXPIfNecessary(@NonNull UserBaseKey userBaseKey, int newXpTotal)
    {
        UserProfileDTO userProfileDTO = getCachedValue(userBaseKey);
        if(userProfileDTO != null && userProfileDTO.currentXP < newXpTotal)
        {
            userProfileDTO.currentXP = newXpTotal;
            onNext(userBaseKey, userProfileDTO);
        }
    }

    public void addAchievements(@NonNull UserBaseKey userBaseKey, int count)
    {
        if (count <= 0)
        {
            throw new IllegalArgumentException("Cannot handle count=" + count);
        }
        UserProfileDTO userProfileDTO = getCachedValue(userBaseKey);
        if(userProfileDTO != null)
        {
            userProfileDTO.achievementCount += count;
            onNext(userBaseKey, userProfileDTO);
        }
    }
}
