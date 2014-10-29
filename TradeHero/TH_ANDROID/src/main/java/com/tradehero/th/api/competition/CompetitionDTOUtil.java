package com.tradehero.th.api.competition;

import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompetitionDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public CompetitionDTOUtil()
    {
        super();
    }
    //</editor-fold>

    @NotNull public CompetitionLeaderboardId getCompetitionLeaderboardId(
            @NotNull ProviderId providerId,
            @NotNull CompetitionId competitionId)
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key);
    }

    @NotNull public CompetitionLeaderboardId getCompetitionLeaderboardId(
            @NotNull ProviderId providerId,
            @NotNull CompetitionId competitionId,
            @Nullable Integer page)
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key, page);
    }

    @NotNull public CompetitionLeaderboardId getCompetitionLeaderboardId(
            @NotNull ProviderId providerId,
            @NotNull CompetitionId competitionId,
            @Nullable Integer page, @Nullable
    Integer perPage)
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key, page, perPage);
    }

    @Contract("!null, null -> null; !null, !null -> !null") @Nullable
    public List<CompetitionLeaderboardId> getCompetitionLeaderboardIds(
            @NotNull ProviderId providerId,
            @Nullable List<CompetitionId> competitionIds)
    {
        if (competitionIds == null)
        {
            return null;
        }
        List<CompetitionLeaderboardId> list = new ArrayList<>();
        for (@NotNull CompetitionId competitionId : competitionIds)
        {
            list.add(getCompetitionLeaderboardId(providerId, competitionId));
        }
        return list;
    }

    @Contract("!null, null, _ -> null; !null, !null, _ -> !null") @Nullable
    public List<CompetitionLeaderboardId> getCompetitionLeaderboardIds(
            @NotNull ProviderId providerId,
            @Nullable List<CompetitionId> competitionIds,
            @Nullable Integer page)
    {
        if (competitionIds == null)
        {
            return null;
        }
        List<CompetitionLeaderboardId> list = new ArrayList<>();
        for (@NotNull CompetitionId competitionId : competitionIds)
        {
            list.add(getCompetitionLeaderboardId(providerId, competitionId, page));
        }
        return list;
    }

    @Contract("!null, null, _, _ -> null; !null, !null, _, _ -> !null") @Nullable
    public List<CompetitionLeaderboardId> getCompetitionLeaderboardIds(
            @NotNull ProviderId providerId,
            @Nullable List<CompetitionId> competitionIds,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        if (competitionIds == null)
        {
            return null;
        }
        List<CompetitionLeaderboardId> list = new ArrayList<>();
        for (@NotNull CompetitionId competitionId : competitionIds)
        {
            list.add(getCompetitionLeaderboardId(providerId, competitionId, page, perPage));
        }
        return list;
    }
}
