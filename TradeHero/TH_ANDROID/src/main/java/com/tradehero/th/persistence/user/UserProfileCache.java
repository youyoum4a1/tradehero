package com.tradehero.th.persistence.user;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import com.tradehero.th.persistence.notification.NotificationListCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache @Deprecated
public class UserProfileCache extends StraightDTOCacheNew<UserBaseKey, UserProfileDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NonNull private final Lazy<UserServiceWrapper> userServiceWrapper;
    @NonNull private final Lazy<UserProfileCompactCacheRx> userProfileCompactCache;
    @NonNull private final Lazy<HeroListCacheRx> heroListCache;
    @NonNull private final Lazy<LeaderboardCacheRx> leaderboardCache;
    @NonNull private final Lazy<MessageHeaderListCacheRx> messageHeaderListCache;
    @NonNull private final Lazy<NotificationListCacheRx> notificationListCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCache(
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<UserProfileCompactCacheRx> userProfileCompactCache,
            @NonNull Lazy<HeroListCacheRx> heroListCache,
            @NonNull Lazy<LeaderboardCacheRx> leaderboardCache,
            @NonNull Lazy<MessageHeaderListCacheRx> messageHeaderListCache,
            @NonNull Lazy<NotificationListCacheRx> notificationListCache,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.userProfileCompactCache = userProfileCompactCache;
        this.heroListCache = heroListCache;
        this.leaderboardCache = leaderboardCache;
        this.messageHeaderListCache = messageHeaderListCache;
        this.notificationListCache = notificationListCache;
    }
    //</editor-fold>

    @Override @NonNull public UserProfileDTO fetch(@NonNull UserBaseKey key) throws Throwable
    {
        VisitedFriendListPrefs.addVisitedId(key);
        return userServiceWrapper.get().getUser(key);
    }

    @Override public UserProfileDTO put(@NonNull UserBaseKey userBaseKey, @NonNull UserProfileDTO userProfileDTO)
    {
        heroListCache.get().invalidate(userBaseKey);
        if (userProfileDTO.mostSkilledLbmu != null)
        {
            leaderboardCache.get().onNext(userProfileDTO.getMostSkilledUserOnLbmuKey(), userProfileDTO.mostSkilledLbmu);
        }
        userProfileCompactCache.get().onNext(userBaseKey, userProfileDTO);
        UserProfileDTO previous = super.put(userBaseKey, userProfileDTO);
        if (previous != null)
        {
            if (previous.unreadMessageThreadsCount != userProfileDTO.unreadMessageThreadsCount)
            {
                messageHeaderListCache.get().invalidateAll();
            }
            if (previous.unreadNotificationsCount != userProfileDTO.unreadNotificationsCount)
            {
                notificationListCache.get().invalidateAll();
            }
        }
        return previous;
    }

    public void updateXPIfNecessary(@NonNull UserBaseKey userBaseKey, int newXpTotal)
    {
        UserProfileDTO userProfileDTO = get(userBaseKey);
        if(userProfileDTO != null && userProfileDTO.currentXP < newXpTotal)
        {
            userProfileDTO.currentXP = newXpTotal;
            put(userBaseKey, userProfileDTO);
        }
    }

    public void addAchievements(@NonNull UserBaseKey userBaseKey, int count)
    {
        if (count <= 0)
        {
            throw new IllegalArgumentException("Cannot handle count=" + count);
        }
        UserProfileDTO userProfileDTO = get(userBaseKey);
        if(userProfileDTO != null)
        {
            userProfileDTO.achievementCount += count;
            put(userBaseKey, userProfileDTO);
        }
    }

    @NonNull public Observable<UserProfileDTO> createObservable(@NonNull UserBaseKey key)
    {
        UserProfileDTO cached = get(key);
        if (cached != null)
        {
            return Observable.just(cached);
        }
        return userServiceWrapper.get().getUserRx(key);
    }
}
