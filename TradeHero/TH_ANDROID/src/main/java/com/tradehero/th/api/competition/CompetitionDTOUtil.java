package com.tradehero.th.api.competition;

import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class CompetitionDTOUtil
{
    @Inject public CompetitionDTOUtil()
    {
        super();
    }

    public CompetitionLeaderboardId getCompetitionLeaderboardId(ProviderId providerId, CompetitionId competitionId)
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key);
    }

    public CompetitionLeaderboardId getCompetitionLeaderboardId(ProviderId providerId, CompetitionId competitionId, Integer page)
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key, page);
    }

    public CompetitionLeaderboardId getCompetitionLeaderboardId(ProviderId providerId, CompetitionId competitionId, Integer page, Integer perPage)
    {
        return new CompetitionLeaderboardId(providerId.key, competitionId.key, page, perPage);
    }

    public List<CompetitionLeaderboardId> getCompetitionLeaderboardIds(ProviderId providerId, List<CompetitionId> competitionIds)
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

    public List<CompetitionLeaderboardId> getCompetitionLeaderboardIds(ProviderId providerId, List<CompetitionId> competitionIds, Integer page)
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

    public List<CompetitionLeaderboardId> getCompetitionLeaderboardIds(ProviderId providerId, List<CompetitionId> competitionIds, Integer page, Integer perPage)
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
