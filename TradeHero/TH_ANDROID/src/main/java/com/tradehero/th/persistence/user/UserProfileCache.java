package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.persistence.social.HeroKey;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class UserProfileCache extends StraightDTOCache<UserBaseKey, UserProfileDTO>
{
    public static final String TAG = UserProfileCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<UserService> userService;
    @Inject protected Lazy<HeroListCache> heroListCache;
    @Inject protected Lazy<LeaderboardCache> leaderboardCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserProfileDTO fetch(UserBaseKey key) throws Throwable
    {
        VisitedFriendListPrefs.addVisitedId(key);
        return userService.get().getUser(key.key);
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
        heroListCache.get().invalidate(new HeroKey(userProfileDTO.getBaseKey(), HeroType.ALL));
        if (userProfileDTO.mostSkilledLbmu != null)
        {
            leaderboardCache.get().put(userProfileDTO.getMostSkilledLbmuKey(), userProfileDTO.mostSkilledLbmu);
        }
        return super.put(userBaseKey, userProfileDTO);
    }
}
