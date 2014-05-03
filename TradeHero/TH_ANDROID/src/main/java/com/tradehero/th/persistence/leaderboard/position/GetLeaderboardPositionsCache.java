package com.tradehero.th.persistence.leaderboard.position;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.leaderboard.position.GetLeaderboardPositionsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.leaderboard.position.PagedLeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.retrofit.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.LeaderboardService;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;
import timber.log.Timber;


@Singleton public class GetLeaderboardPositionsCache extends PartialDTOCache<LeaderboardMarkUserId, GetLeaderboardPositionsDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private THLruCache<LeaderboardMarkUserId, GetLeaderboardPositionsCutDTO> lruCache;
    @Inject protected Lazy<LeaderboardService> leaderboardService;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<LeaderboardPositionCache> filedPositionCache;

    //<editor-fold desc="Constructors">
    @Inject public GetLeaderboardPositionsCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public GetLeaderboardPositionsCache(int maxSize)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    @Override protected GetLeaderboardPositionsDTO fetch(LeaderboardMarkUserId key)
    {
        GetLeaderboardPositionsDTO getLeaderboardPositionsDTO = null;
        try
        {
            getLeaderboardPositionsDTO = fetchInternal(key);
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            Timber.e("Error requesting key %s", key, retrofitError);
        }
        return getLeaderboardPositionsDTO;
    }

    protected GetLeaderboardPositionsDTO fetchInternal(LeaderboardMarkUserId key) throws RetrofitError
    {
        GetLeaderboardPositionsDTO fetched = null;
        if (key instanceof PerPagedLeaderboardMarkUserId)
        {
            fetched = leaderboardService.get().getPositionsForLeaderboardMarkUser(key.key, ((PerPagedLeaderboardMarkUserId) key).page,
                    ((PerPagedLeaderboardMarkUserId) key).perPage);
        }
        else if (key instanceof PagedLeaderboardMarkUserId)
        {
            fetched = leaderboardService.get().getPositionsForLeaderboardMarkUser(key.key, ((PagedLeaderboardMarkUserId) key).page);
        }
        else
        {
            fetched = leaderboardService.get().getPositionsForLeaderboardMarkUser(key.key);
        }
        if (fetched != null)
        {
            fetched.setLeaderboardMarkUserId(key);
        }
        return fetched;
    }

    @Override public GetLeaderboardPositionsDTO get(LeaderboardMarkUserId key)
    {
        GetLeaderboardPositionsCutDTO getPositionsCutDTO = this.lruCache.get(key);
        if (getPositionsCutDTO == null)
        {
            return null;
        }
        return getPositionsCutDTO.create(securityCompactCache.get(), filedPositionCache.get());
    }

    @Override public GetLeaderboardPositionsDTO put(LeaderboardMarkUserId key, GetLeaderboardPositionsDTO value)
    {
        // We invalidate the previous list of positions before it get updated
        invalidateMatchingPositionCache(get(key));

        GetLeaderboardPositionsDTO previous = null;

        GetLeaderboardPositionsCutDTO previousCut = lruCache.put(
                key,
                new GetLeaderboardPositionsCutDTO(
                        value,
                        securityCompactCache.get(),
                        filedPositionCache.get()));

        if (previousCut != null)
        {
            previous = previousCut.create(securityCompactCache.get(), filedPositionCache.get());
        }

        return previous;
    }

    @Override public void invalidate(LeaderboardMarkUserId key)
    {
        invalidateMatchingPositionCache(get(key));
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    protected void invalidateMatchingPositionCache(GetLeaderboardPositionsDTO value)
    {
        if (value != null && value.positions != null)
        {
            for (PositionInPeriodDTO positionInPeriodDTO : value.positions)
            {
                filedPositionCache.get().invalidate(positionInPeriodDTO.getLbOwnedPositionId());
            }
        }
    }

    private static class GetLeaderboardPositionsCutDTO
    {
        public List<OwnedLeaderboardPositionId> ownedLeaderboardPositionIds;
        public List<SecurityId> securityIds;
        public int openPositionsCount;
        public int closedPositionsCount;

        public GetLeaderboardPositionsCutDTO(
                GetLeaderboardPositionsDTO getLeaderboardPositionsDTO,
                SecurityCompactCache securityCompactCache,
                LeaderboardPositionCache leaderboardPositionCache)
        {
            leaderboardPositionCache.put(getLeaderboardPositionsDTO.positions);
            this.ownedLeaderboardPositionIds = PositionInPeriodDTO.getFiledLbPositionIds(getLeaderboardPositionsDTO.positions);

            securityCompactCache.put(getLeaderboardPositionsDTO.securities);
            this.securityIds = SecurityCompactDTO.getSecurityIds(getLeaderboardPositionsDTO.securities);
            
            this.openPositionsCount = getLeaderboardPositionsDTO.openPositionsCount;
            this.closedPositionsCount = getLeaderboardPositionsDTO.closedPositionsCount;
        }

        public GetLeaderboardPositionsDTO create(
                SecurityCompactCache securityCompactCache,
                LeaderboardPositionCache leaderboardPositionCache)
        {
            return new GetLeaderboardPositionsDTO(
                    leaderboardPositionCache.get(ownedLeaderboardPositionIds),
                    securityCompactCache.get(securityIds),
                    openPositionsCount,
                    closedPositionsCount
            );
        }
    }
}
