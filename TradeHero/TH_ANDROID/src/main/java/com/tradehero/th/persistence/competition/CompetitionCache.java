package com.tradehero.th.persistence.competition;

import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardUserCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class CompetitionCache extends StraightCutDTOCacheNew<CompetitionId, CompetitionDTO, CompetitionCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final CompetitionServiceWrapper competitionServiceWrapper;
    @NotNull private final Lazy<LeaderboardDefCache> leaderboardDefCache;
    @NotNull private final Lazy<LeaderboardUserCache> leaderboardUserCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionCache(
            @NotNull CompetitionServiceWrapper competitionServiceWrapper,
            @NotNull Lazy<LeaderboardDefCache> leaderboardDefCache,
            @NotNull Lazy<LeaderboardUserCache> leaderboardUserCache)
    {
        this(DEFAULT_MAX_SIZE, competitionServiceWrapper, leaderboardDefCache, leaderboardUserCache);
    }

    public CompetitionCache(
            int maxSize,
            @NotNull CompetitionServiceWrapper competitionServiceWrapper,
            @NotNull Lazy<LeaderboardDefCache> leaderboardDefCache,
            @NotNull Lazy<LeaderboardUserCache> leaderboardUserCache)
    {
        super(maxSize);
        this.competitionServiceWrapper = competitionServiceWrapper;
        this.leaderboardDefCache = leaderboardDefCache;
        this.leaderboardUserCache = leaderboardUserCache;
    }
    //</editor-fold>

    @Override @NotNull public CompetitionDTO fetch(@NotNull CompetitionId key) throws Throwable
    {
        return competitionServiceWrapper.getCompetition(key);
    }

    @Override @NotNull protected CompetitionCutDTO cutValue(@NotNull CompetitionId key, @NotNull CompetitionDTO value)
    {
        return new CompetitionCutDTO(value, leaderboardDefCache.get(), leaderboardUserCache.get());
    }

    @Nullable @Override protected CompetitionDTO inflateValue(@NotNull CompetitionId key, @Nullable CompetitionCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(leaderboardDefCache.get(), leaderboardUserCache.get());
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public CompetitionDTOList get(@Nullable List<CompetitionId> competitionIds)
    {
        if (competitionIds == null)
        {
            return null;
        }

        CompetitionDTOList fleshedValues = new CompetitionDTOList();
        for (@NotNull CompetitionId competitionId : competitionIds)
        {
            fleshedValues.add(get(competitionId));
        }
        return fleshedValues;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public CompetitionDTOList put(@Nullable List<CompetitionDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        CompetitionDTOList previousValues = new CompetitionDTOList();
        for (@NotNull CompetitionDTO competitionDTO : values)
        {
            previousValues.add(put(competitionDTO.getCompetitionId(), competitionDTO));
        }
        return previousValues;
    }

    public MiddleCallback<UserCompetitionDTO> creatUGCompetition(@NotNull String name, @NotNull String description, @NotNull int durationDays,
            @NotNull int[] exchangeIds, @Nullable
    Callback<UserCompetitionDTO> callback)
    {
        return competitionServiceWrapper.creatUGC(name, description, durationDays, exchangeIds, callback);
    }

    public MiddleCallback<UserCompetitionDTO> enrollUGCompetition(int competitionId, @Nullable
    Callback<UserCompetitionDTO> callback)
    {
        return competitionServiceWrapper.enrollUGCompetition(competitionId, callback);
    }

    public MiddleCallback<UserCompetitionDTO> getCompetitionDetail(int competitionId, @Nullable
    Callback<UserCompetitionDTO> callback)
    {
        return competitionServiceWrapper.getCompetitionDetail(competitionId, callback);
    }

    public MiddleCallback<LeaderboardDTO> getMySelfRank(int leaderboardsId, int userId, @Nullable
    Callback<LeaderboardDTO> callback)
    {
        return competitionServiceWrapper.getMySelfRank(leaderboardsId, userId, callback);
    }

    //public MiddleCallback<UserCompetitionDTOList> getUserCompetitions(@NotNull int page, @NotNull int perpage,
    //        Callback<UserCompetitionDTOList> callback)
    //{
    //    return competitionServiceWrapper.getUserCompetitions(page, perpage, callback);
    //}
    //
    //public MiddleCallback<UserCompetitionDTOList> getOfficalCompetitions(
    //        Callback<UserCompetitionDTOList> callback)
    //{
    //    return competitionServiceWrapper.getOfficalCompetitions(callback);
    //}
    //
    //public MiddleCallback<UserCompetitionDTOList> getMyCompetitions(@NotNull int page, @NotNull int perpage,
    //        Callback<UserCompetitionDTOList> callback)
    //{
    //    return competitionServiceWrapper.getMyCompetitions(page, perpage, callback);
    //}
    //
    //public MiddleCallback<UserCompetitionDTOList> getVipCompetitions(
    //        Callback<UserCompetitionDTOList> callback)
    //{
    //    return competitionServiceWrapper.getVipCompetitions(callback);
    //}
}
