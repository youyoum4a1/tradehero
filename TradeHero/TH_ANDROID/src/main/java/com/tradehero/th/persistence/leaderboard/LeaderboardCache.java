package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOUtil;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import dagger.Lazy;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LeaderboardCache extends PartialDTOCache<LeaderboardKey, LeaderboardDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private THLruCache<LeaderboardKey, LeaderboardCutDTO> lruCache;
    @Inject protected Lazy<LeaderboardUserCache> leaderboardUserCache;
    @Inject protected LeaderboardUserDTOUtil leaderboardUserDTOUtil;
    @Inject protected LeaderboardServiceWrapper leaderboardServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public LeaderboardCache(int maxSize)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    protected LeaderboardDTO fetch(LeaderboardKey key) throws Throwable
    {
        return leaderboardServiceWrapper.getLeaderboard(key);
    }

    @Override public LeaderboardDTO get(LeaderboardKey key)
    {
        LeaderboardCutDTO leaderboardCutDTO = this.lruCache.get(key);
        if (leaderboardCutDTO == null)
        {
            return null;
        }
        LeaderboardDTO value =  leaderboardCutDTO.create(leaderboardUserCache.get());
        return value;
    }

    @Override public LeaderboardDTO put(LeaderboardKey key, LeaderboardDTO value)
    {
        LeaderboardDTO previous = null;

        LeaderboardCutDTO previousCut = lruCache.put(
                key,
                new LeaderboardCutDTO(
                        value,
                        leaderboardUserCache.get(),
                        leaderboardUserDTOUtil));

        if (previousCut != null)
        {
            previous = previousCut.create(leaderboardUserCache.get());
        }

        return previous;
    }

    @Override public void invalidate(LeaderboardKey key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    // The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
    // It is static so as not to keep a link back to the cache instance.
    private static class LeaderboardCutDTO
    {
        public int id;
        public String name;
        public List<LeaderboardUserId> userIds;
        public int userIsAtPositionZeroBased;
        public Date markUtc;
        

        public LeaderboardCutDTO(
                LeaderboardDTO leaderboardDTO,
                LeaderboardUserCache leaderboardUserCache,
                LeaderboardUserDTOUtil leaderboardUserDTOUtil)
        {
            this.id = leaderboardDTO.id;
            this.name = leaderboardDTO.name;

            leaderboardUserCache.put(leaderboardUserDTOUtil.map(leaderboardDTO.users));
            userIds = leaderboardUserDTOUtil.getIds(leaderboardDTO.users);

            this.userIsAtPositionZeroBased = leaderboardDTO.userIsAtPositionZeroBased;
            this.markUtc = leaderboardDTO.markUtc;
        }

        public LeaderboardDTO create(LeaderboardUserCache leaderboardUserCache)
        {
            return new LeaderboardDTO(
                    id,
                    name,
                    leaderboardUserCache.get(userIds),
                    userIsAtPositionZeroBased,
                    markUtc
            );
        }
    }
}
