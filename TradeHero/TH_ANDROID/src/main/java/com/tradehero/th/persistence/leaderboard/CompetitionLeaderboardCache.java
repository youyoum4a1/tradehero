package com.tradehero.th.persistence.leaderboard;

import android.support.v4.util.LruCache;
import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.leaderboard.LeaderboardKey;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 22/1/14 Time: 4:47 PM To change this template use File | Settings | File Templates. */
@Singleton public class CompetitionLeaderboardCache extends PartialDTOCache<CompetitionLeaderboardId, CompetitionLeaderboardDTO>
{
    public static final String TAG = CompetitionLeaderboardCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private LruCache<CompetitionLeaderboardId, CompetitionLeaderboardCache.CompetitionLeaderboardCutDTO> lruCache;
    @Inject protected CompetitionServiceWrapper competitionServiceWrapper;
    @Inject protected LeaderboardCache leaderboardCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionLeaderboardCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public CompetitionLeaderboardCache(int maxSize)
    {
        super();
        lruCache = new LruCache<>(maxSize);
    }
    //</editor-fold>

    protected CompetitionLeaderboardDTO fetch(CompetitionLeaderboardId key) throws Throwable
    {
        return competitionServiceWrapper.getCompetitionLeaderboard(key);
    }

    @Override public CompetitionLeaderboardDTO get(CompetitionLeaderboardId key)
    {
        CompetitionLeaderboardCutDTO competitionLeaderboardCutDTO = this.lruCache.get(key);
        if (competitionLeaderboardCutDTO == null)
        {
            return null;
        }
        return competitionLeaderboardCutDTO.create(leaderboardCache);
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

    public List<CompetitionLeaderboardDTO> getOrFetch(List<CompetitionLeaderboardId> competitionLeaderboardIds) throws Throwable
    {
        if (competitionLeaderboardIds == null)
        {
            return null;
        }

        List<CompetitionLeaderboardDTO> fleshedValues = new ArrayList<>();

        for (CompetitionLeaderboardId competitionLeaderboardId: competitionLeaderboardIds)
        {
            fleshedValues.add(getOrFetch(competitionLeaderboardId));
        }

        return fleshedValues;
    }

    @Override public CompetitionLeaderboardDTO put(CompetitionLeaderboardId key, CompetitionLeaderboardDTO value)
    {
        CompetitionLeaderboardDTO previous = null;

        CompetitionLeaderboardCutDTO previousCut = lruCache.put(
                key,
                new CompetitionLeaderboardCutDTO(value, leaderboardCache));

        if (previousCut != null)
        {
            previous = previousCut.create(leaderboardCache);
        }

        return previous;
    }

    @Override public void invalidate(CompetitionLeaderboardId key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    // The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
    // It is static so as not to keep a link back to the cache instance.
    private static class CompetitionLeaderboardCutDTO
    {
        public LeaderboardKey leaderboardKey;
        public List<AdDTO> ads;
        public int adFrequencyRows;
        public int adStartRow;
        public List<PrizeDTO> prizes;

        public CompetitionLeaderboardCutDTO(
                CompetitionLeaderboardDTO competitionLeaderboardDTO,
                LeaderboardCache leaderboardCache)
        {
            if (competitionLeaderboardDTO.leaderboard != null)
            {
                leaderboardCache.put(competitionLeaderboardDTO.leaderboard.getLeaderboardKey(), competitionLeaderboardDTO.leaderboard);
                this.leaderboardKey = competitionLeaderboardDTO.leaderboard.getLeaderboardKey();
            }
            else
            {
                this.leaderboardKey = null;
            }

            ads = competitionLeaderboardDTO.ads;
            adFrequencyRows = competitionLeaderboardDTO.adFrequencyRows;
            adStartRow = competitionLeaderboardDTO.adStartRow;
            prizes = competitionLeaderboardDTO.prizes;
        }

        public CompetitionLeaderboardDTO create(LeaderboardCache leaderboardCache)
        {
            return new CompetitionLeaderboardDTO(
                    leaderboardKey != null ? leaderboardCache.get(leaderboardKey) : null,
                    ads,
                    adFrequencyRows,
                    adStartRow,
                    prizes
            );
        }
    }
}
