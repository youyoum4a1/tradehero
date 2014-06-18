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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserProfileCache extends StraightDTOCacheNew<UserBaseKey, UserProfileDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final Lazy<UserServiceWrapper> userServiceWrapper;
    @NotNull private final Lazy<UserProfileCompactCache> userProfileCompactCache;
    @NotNull private final Lazy<HeroListCache> heroListCache;
    @NotNull private final Lazy<LeaderboardCache> leaderboardCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCache(
            @NotNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NotNull Lazy<UserProfileCompactCache> userProfileCompactCache,
            @NotNull Lazy<HeroListCache> heroListCache,
            @NotNull Lazy<LeaderboardCache> leaderboardCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.userServiceWrapper = userServiceWrapper;
        this.userProfileCompactCache = userProfileCompactCache;
        this.heroListCache = heroListCache;
        this.leaderboardCache = leaderboardCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        VisitedFriendListPrefs.addVisitedId(key);
        return userServiceWrapper.get().getUser(key);
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<UserProfileDTO> getOrFetchSync(@Nullable List<UserBaseKey> baseKeys) throws Throwable
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserProfileDTO> userProfileDTOs = new ArrayList<>();
        for (@NotNull UserBaseKey baseKey: baseKeys)
        {
            userProfileDTOs.add(getOrFetchSync(baseKey, false));
        }
        return userProfileDTOs;
    }

    @Override public UserProfileDTO put(@NotNull UserBaseKey userBaseKey, @NotNull UserProfileDTO userProfileDTO)
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
