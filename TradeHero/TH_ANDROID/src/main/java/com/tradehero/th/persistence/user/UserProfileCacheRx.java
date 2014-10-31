package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class UserProfileCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, UserProfileDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final Lazy<UserServiceWrapper> userServiceWrapper;
    @NotNull private final Lazy<UserProfileCompactCacheRx> userProfileCompactCache;
    @NotNull private final Lazy<LeaderboardCache> leaderboardCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCacheRx(
            @NotNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NotNull Lazy<UserProfileCompactCacheRx> userProfileCompactCache,
            @NotNull Lazy<LeaderboardCache> leaderboardCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.userProfileCompactCache = userProfileCompactCache;
        this.leaderboardCache = leaderboardCache;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<UserProfileDTO> fetch(@NotNull UserBaseKey key)
    {
        VisitedFriendListPrefs.addVisitedId(key);
        return userServiceWrapper.get().getUserRx(key);
    }

    @Override public void onNext(@NotNull UserBaseKey key, @NotNull UserProfileDTO userProfileDTO)
    {
        if (userProfileDTO.mostSkilledLbmu != null)
        {
            leaderboardCache.get().put(userProfileDTO.getMostSkilledUserOnLbmuKey(), userProfileDTO.mostSkilledLbmu);
        }
        userProfileCompactCache.get().onNext(key, userProfileDTO);
        super.onNext(key, userProfileDTO);
    }

    public void updateXPIfNecessary(@NotNull UserBaseKey userBaseKey, int newXpTotal)
    {
        UserProfileDTO userProfileDTO = getValue(userBaseKey);
        if(userProfileDTO != null && userProfileDTO.currentXP < newXpTotal)
        {
            userProfileDTO.currentXP = newXpTotal;
            onNext(userBaseKey, userProfileDTO);
        }
    }

    public void addAchievements(@NotNull UserBaseKey userBaseKey, int count)
    {
        if (count <= 0)
        {
            throw new IllegalArgumentException("Cannot handle count=" + count);
        }
        UserProfileDTO userProfileDTO = getValue(userBaseKey);
        if(userProfileDTO != null)
        {
            userProfileDTO.achievementCount += count;
            onNext(userBaseKey, userProfileDTO);
        }
    }
}
