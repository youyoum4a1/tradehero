package com.tradehero.th.persistence.leaderboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserIdList;
import com.tradehero.th.api.users.SuggestHeroesListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class LeaderboardUserListCache
        extends StraightCutDTOCacheNew<
        SuggestHeroesListType,
        LeaderboardUserDTOList,
        LeaderboardUserIdList>
{
    private static final int MAX_SIZE = 10;

    @NonNull private final Lazy<LeaderboardUserCacheRx> leaderboardUserCache;
    @NonNull private final UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardUserListCache(
            @NonNull Lazy<LeaderboardUserCacheRx> leaderboardUserCache,
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
        this.leaderboardUserCache = leaderboardUserCache;
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override public LeaderboardUserDTOList fetch(@NonNull SuggestHeroesListType key) throws Throwable
    {
        return userServiceWrapper.suggestHeroes(key);
    }

    @NonNull @Override protected LeaderboardUserIdList cutValue(
            @NonNull SuggestHeroesListType key,
            @NonNull LeaderboardUserDTOList value)
    {
        leaderboardUserCache.get().put(value);
        return new LeaderboardUserIdList(value, (LeaderboardUserDTO) null);
    }

    @Nullable @Override protected LeaderboardUserDTOList inflateValue(
            @NonNull SuggestHeroesListType key,
            @Nullable LeaderboardUserIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        LeaderboardUserDTOList cached = new LeaderboardUserDTOList();
        for (LeaderboardUserId id : cutValue)
        {
            cached.add(leaderboardUserCache.get().getValue(id));
        }

        if (cached.hasNullItem())
        {
            return null;
        }
        return cached;
    }
}
