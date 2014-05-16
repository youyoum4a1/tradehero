package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
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

@Singleton public class UserProfileCache extends StraightDTOCache<UserBaseKey, UserProfileDTO>
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

    @Override public GetOrFetchTask<UserBaseKey, UserProfileDTO> getOrFetch(
            UserBaseKey key, boolean forceUpdateCache,
            Listener<UserBaseKey, UserProfileDTO> initialListener)
    {
        if (key == null)
        {
            throw new NullPointerException("UserBaseKey cannot be null");
        }
        return super.getOrFetch(key, forceUpdateCache, initialListener);
    }

    @Override protected UserProfileDTO fetch(UserBaseKey key) throws Throwable
    {
        VisitedFriendListPrefs.addVisitedId(key);
        return userServiceWrapper.get().getUser(key);
    }

    public List<UserProfileDTO> getOrFetch(List<UserBaseKey> baseKeys) throws Throwable
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserProfileDTO> userProfileDTOs = new ArrayList<>();
        for (UserBaseKey baseKey: baseKeys)
        {
            userProfileDTOs.add(getOrFetch(baseKey, false));
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
