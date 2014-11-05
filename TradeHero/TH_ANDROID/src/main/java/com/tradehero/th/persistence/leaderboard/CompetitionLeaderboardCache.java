package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton @UserCache
public class CompetitionLeaderboardCache extends StraightCutDTOCacheNew<CompetitionLeaderboardId, CompetitionLeaderboardDTO, CompetitionLeaderboardCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NonNull private final CompetitionServiceWrapper competitionServiceWrapper;
    @NonNull private final LeaderboardCache leaderboardCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionLeaderboardCache(
            @NonNull CompetitionServiceWrapper competitionServiceWrapper,
            @NonNull LeaderboardCache leaderboardCache,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        this(DEFAULT_MAX_SIZE, competitionServiceWrapper, leaderboardCache, dtoCacheUtil);
    }

    public CompetitionLeaderboardCache(
            int maxSize,
            @NonNull CompetitionServiceWrapper competitionServiceWrapper,
            @NonNull LeaderboardCache leaderboardCache,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
        this.competitionServiceWrapper = competitionServiceWrapper;
        this.leaderboardCache = leaderboardCache;
    }
    //</editor-fold>

    @Override @NonNull public CompetitionLeaderboardDTO fetch(@NonNull CompetitionLeaderboardId key) throws Throwable
    {
        return competitionServiceWrapper.getCompetitionLeaderboard(key);
    }

    @NonNull @Override protected CompetitionLeaderboardCutDTO cutValue(
            @NonNull CompetitionLeaderboardId key,
            @NonNull CompetitionLeaderboardDTO value)
    {
        return new CompetitionLeaderboardCutDTO(value, leaderboardCache);
    }

    @Nullable @Override protected CompetitionLeaderboardDTO inflateValue(
            @NonNull CompetitionLeaderboardId key,
            @Nullable CompetitionLeaderboardCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(leaderboardCache);
    }
}
