package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class CompetitionLeaderboardCache extends StraightDTOCache<CompetitionLeaderboardId, CompetitionLeaderboardDTO>
{
    public static final String TAG = CompetitionLeaderboardCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected CompetitionServiceWrapper competitionServiceWrapper;

    // TODO save memory by splitting into different caches?

    //<editor-fold desc="Constructors">
    @Inject public CompetitionLeaderboardCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected CompetitionLeaderboardDTO fetch(CompetitionLeaderboardId key) throws Throwable
    {
        return this.competitionServiceWrapper.getCompetitionLeaderboard(key);
    }

    public List<CompetitionLeaderboardDTO> getOrFetch(List<CompetitionLeaderboardId> competitionLeaderboardIds) throws Throwable
    {
        if (competitionLeaderboardIds == null)
        {
            return null;
        }

        List<CompetitionLeaderboardDTO> competitionLeaderboardDTOs = new ArrayList<>();
        for (CompetitionLeaderboardId competitionLeaderboardId : competitionLeaderboardIds)
        {
            competitionLeaderboardDTOs.add(getOrFetch(competitionLeaderboardId, false));
        }
        return competitionLeaderboardDTOs;
    }

    public List<CompetitionLeaderboardDTO> get(List<CompetitionLeaderboardId> competitionLeaderboardIds)
    {
        if (competitionLeaderboardIds == null)
        {
            return null;
        }

        List<CompetitionLeaderboardDTO> fleshedValues = new ArrayList<>();

        for (CompetitionLeaderboardId competitionLeaderboardId: competitionLeaderboardIds)
        {
            fleshedValues.add(get(competitionLeaderboardId));
        }

        return fleshedValues;
    }
}
