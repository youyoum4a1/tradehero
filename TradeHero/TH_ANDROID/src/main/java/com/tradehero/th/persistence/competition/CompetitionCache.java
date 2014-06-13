package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOUtil;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class CompetitionCache extends PartialDTOCache<CompetitionId, CompetitionDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private THLruCache<CompetitionId, CompetitionCache.CompetitionCutDTO> lruCache;
    @Inject protected LeaderboardDefCache leaderboardDefCache;
    @Inject protected LeaderboardUserDTOUtil leaderboardUserDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public CompetitionCache(int maxSize)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    protected CompetitionDTO fetch(CompetitionId key) throws Throwable
    {
        throw new IllegalStateException("There is no fetch on this cache");
    }

    @Override public CompetitionDTO get(CompetitionId key)
    {
        CompetitionCutDTO leaderboardCutDTO = this.lruCache.get(key);
        if (leaderboardCutDTO == null)
        {
            return null;
        }
        return leaderboardCutDTO.create(leaderboardDefCache);
    }

    @Override public CompetitionDTO put(CompetitionId key, CompetitionDTO value)
    {
        CompetitionDTO previous = null;

        CompetitionCutDTO previousCut = lruCache.put(
                key,
                new CompetitionCutDTO(value, leaderboardDefCache));

        if (previousCut != null)
        {
            previous = previousCut.create(leaderboardDefCache);
        }

        return previous;
    }

    public List<CompetitionDTO> get(List<CompetitionId> competitionIds)
    {
        if (competitionIds == null)
        {
            return null;
        }

        List<CompetitionDTO> fleshedValues = new ArrayList<>();

        for (CompetitionId competitionId: competitionIds)
        {
            fleshedValues.add(get(competitionId));
        }

        return fleshedValues;
    }

    public List<CompetitionDTO> put(List<CompetitionDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<CompetitionDTO> previousValues = new ArrayList<>();

        for (CompetitionDTO competitionDTO: values)
        {
            previousValues.add(put(competitionDTO.getCompetitionId(), competitionDTO));
        }

        return previousValues;
    }

    @Override public void invalidate(CompetitionId key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    // The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
    // It is static so as not to keep a link back to the cache instance.
    private static class CompetitionCutDTO
    {
        public int id;
        public LeaderboardDefKey leaderboardKey;
        public String name;
        public String competitionDurationType;
        public String iconActiveUrl;
        public String iconInactiveUrl;
        public String prizeValueWithCcy;

        public CompetitionCutDTO(
                CompetitionDTO competitionDTO,
                LeaderboardDefCache leaderboardDefCache)
        {
            this.id = competitionDTO.id;

            if (competitionDTO.leaderboard == null)
            {
                leaderboardKey = null;
            }
            else
            {
                LeaderboardDefKey key = competitionDTO.leaderboard.getLeaderboardDefKey();
                leaderboardDefCache.put(key, competitionDTO.leaderboard);
                leaderboardKey = key;
            }

            this.name = competitionDTO.name;
            this.competitionDurationType = competitionDTO.competitionDurationType;
            this.iconActiveUrl = competitionDTO.iconActiveUrl;
            this.iconInactiveUrl = competitionDTO.iconInactiveUrl;
            this.prizeValueWithCcy = competitionDTO.prizeValueWithCcy;
        }

        public CompetitionDTO create(LeaderboardDefCache leaderboardDefCache)
        {
            return new CompetitionDTO(
                    id,
                    leaderboardKey == null ? null : leaderboardDefCache.get(leaderboardKey),
                    name,
                    competitionDurationType,
                    iconActiveUrl,
                    iconInactiveUrl,
                    prizeValueWithCcy
            );
        }
    }
}
