package com.tradehero.th.api.competition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class CompetitionDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public CompetitionDTOUtil()
    {
        super();
    }
    //</editor-fold>

    @NonNull public CompetitionLeaderboardId getCompetitionLeaderboardId(
            @NonNull ProviderId providerId,
            @NonNull CompetitionId competitionId)
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key);
    }

    @NonNull public CompetitionLeaderboardId getCompetitionLeaderboardId(
            @NonNull ProviderId providerId,
            @NonNull CompetitionId competitionId,
            @Nullable Integer page)
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key, page);
    }

    @NonNull public CompetitionLeaderboardId getCompetitionLeaderboardId(
            @NonNull ProviderId providerId,
            @NonNull CompetitionId competitionId,
            @Nullable Integer page, @Nullable
    Integer perPage)
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key, page, perPage);
    }

    @Nullable
    public List<CompetitionLeaderboardId> getCompetitionLeaderboardIds(
            @NonNull ProviderId providerId,
            @Nullable List<CompetitionId> competitionIds)
    {
        if (competitionIds == null)
        {
            return null;
        }
        List<CompetitionLeaderboardId> list = new ArrayList<>();
        for (CompetitionId competitionId : competitionIds)
        {
            list.add(getCompetitionLeaderboardId(providerId, competitionId));
        }
        return list;
    }

    @Nullable
    public List<CompetitionLeaderboardId> getCompetitionLeaderboardIds(
            @NonNull ProviderId providerId,
            @Nullable List<CompetitionId> competitionIds,
            @Nullable Integer page)
    {
        if (competitionIds == null)
        {
            return null;
        }
        List<CompetitionLeaderboardId> list = new ArrayList<>();
        for (CompetitionId competitionId : competitionIds)
        {
            list.add(getCompetitionLeaderboardId(providerId, competitionId, page));
        }
        return list;
    }

    @Nullable
    public List<CompetitionLeaderboardId> getCompetitionLeaderboardIds(
            @NonNull ProviderId providerId,
            @Nullable List<CompetitionId> competitionIds,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        if (competitionIds == null)
        {
            return null;
        }
        List<CompetitionLeaderboardId> list = new ArrayList<>();
        for (CompetitionId competitionId : competitionIds)
        {
            list.add(getCompetitionLeaderboardId(providerId, competitionId, page, perPage));
        }
        return list;
    }
}
