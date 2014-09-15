package com.tradehero.th.fragments.chinabuild.cache;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardUserCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.notification.NotificationListCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.persistence.user.UserProfileCompactCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class CompetitionNewCache extends StraightDTOCacheNew<CompetitionListType, UserCompetitionDTOList>
{
    public static final int DEFAULT_MAX_SIZE = 500;
    @NotNull private final CompetitionServiceWrapper competitionServiceWrapper;


    @Inject public CompetitionNewCache(
            @NotNull CompetitionServiceWrapper competitionServiceWrapper)
    {
        this(DEFAULT_MAX_SIZE,competitionServiceWrapper);
    }

    public CompetitionNewCache(
            int maxSize,
            @NotNull CompetitionServiceWrapper competitionServiceWrapper)
    {
        super(maxSize);
        this.competitionServiceWrapper = competitionServiceWrapper;

    }
    //</editor-fold>

    @Override @NotNull public UserCompetitionDTOList fetch(@NotNull CompetitionListType key) throws Throwable
    {
        return competitionServiceWrapper.getCompetition(key);
    }
    //
    //@Override public UserProfileDTO put(@NotNull UserBaseKey userBaseKey, @NotNull UserProfileDTO userProfileDTO)
    //{
    //    heroListCache.get().invalidate(userBaseKey);
    //    if (userProfileDTO.mostSkilledLbmu != null)
    //    {
    //        leaderboardCache.get().put(userProfileDTO.getMostSkilledLbmuKey(), userProfileDTO.mostSkilledLbmu);
    //    }
    //    userProfileCompactCache.get().put(userBaseKey, userProfileDTO);
    //    UserProfileDTO previous = super.put(userBaseKey, userProfileDTO);
    //    if (previous != null)
    //    {
    //        if (previous.unreadMessageThreadsCount != userProfileDTO.unreadMessageThreadsCount)
    //        {
    //            messageHeaderListCache.get().invalidateAll();
    //        }
    //        if (previous.unreadNotificationsCount != userProfileDTO.unreadNotificationsCount)
    //        {
    //            notificationListCache.get().invalidateAll();
    //        }
    //    }
    //    return previous;
    //}
}
