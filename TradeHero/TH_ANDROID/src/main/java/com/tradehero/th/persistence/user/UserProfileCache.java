package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class UserProfileCache extends StraightDTOCacheNew<UserBaseKey, UserProfileDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject protected Lazy<UserProfileCompactCache> userProfileCompactCache;
    @Inject protected Lazy<HeroListCache> heroListCache;
    @Inject protected Lazy<LeaderboardCache> leaderboardCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override public UserProfileDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        VisitedFriendListPrefs.addVisitedId(key);
        return userServiceWrapper.get().getUser(key);
    }

    public List<UserProfileDTO> getOrFetchSync(List<UserBaseKey> baseKeys) throws Throwable
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserProfileDTO> userProfileDTOs = new ArrayList<>();
        for (UserBaseKey baseKey: baseKeys)
        {
            userProfileDTOs.add(getOrFetchSync(baseKey, false));
        }
        return userProfileDTOs;
    }

    @Override public UserProfileDTO put(UserBaseKey userBaseKey, UserProfileDTO userProfileDTO)
    {
        heroListCache.get().invalidate(userBaseKey);
        if (userProfileDTO.mostSkilledLbmu != null)
        {
            leaderboardCache.get().put(userProfileDTO.getMostSkilledLbmuKey(), userProfileDTO.mostSkilledLbmu);
        }
        userProfileCompactCache.get().put(userBaseKey, userProfileDTO);
        return super.put(userBaseKey, userProfileDTO);
    }
}
