package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserIdList;
import com.tradehero.th.api.users.SuggestHeroesListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class LeaderboardUserListCache
        extends StraightCutDTOCacheNew<
        SuggestHeroesListType,
        LeaderboardUserDTOList,
        LeaderboardUserIdList>
{
    private static final int MAX_SIZE = 10;

    @NotNull private final Lazy<LeaderboardUserCache> leaderboardUserCache;
    @NotNull private final UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardUserListCache(
            @NotNull Lazy<LeaderboardUserCache> leaderboardUserCache,
            @NotNull UserServiceWrapper userServiceWrapper)
    {
        super(MAX_SIZE);
        this.leaderboardUserCache = leaderboardUserCache;
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override public LeaderboardUserDTOList fetch(@NotNull SuggestHeroesListType key) throws Throwable
    {
        return userServiceWrapper.suggestHeroes(key);
    }

    @NotNull @Override protected LeaderboardUserIdList cutValue(
            @NotNull SuggestHeroesListType key,
            @NotNull LeaderboardUserDTOList value)
    {
        leaderboardUserCache.get().put(value);
        return new LeaderboardUserIdList(value, (LeaderboardUserDTO) null);
    }

    @Nullable @Override protected LeaderboardUserDTOList inflateValue(
            @NotNull SuggestHeroesListType key,
            @Nullable LeaderboardUserIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        LeaderboardUserDTOList cached = leaderboardUserCache.get().get(cutValue);
        if (cached.hasNullItem())
        {
            return null;
        }
        return cached;
    }
}
