package com.tradehero.th.persistence.position;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PagedOwnedPortfolioId;
import com.tradehero.th.api.portfolio.PerPagedOwnedPortfolioId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.PositionService;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:44 PM To change this template use File | Settings | File Templates. */
@Singleton public class GetPositionsCache extends PartialDTOCache<OwnedPortfolioId, GetPositionsDTO>
{
    public static final String TAG = GetPositionsCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private LruCache<OwnedPortfolioId, GetPositionsCache.GetPositionsCutDTO> lruCache;
    @Inject protected Lazy<PositionService> positionService;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<PositionCache> filedPositionCache;

    //<editor-fold desc="Constructors">
    @Inject public GetPositionsCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public GetPositionsCache(int maxSize)
    {
        super();
        lruCache = new LruCache<>(maxSize);
    }
    //</editor-fold>

    protected GetPositionsDTO fetch(OwnedPortfolioId key)
    {
        GetPositionsDTO getPositionsDTO = null;
        try
        {
            getPositionsDTO = fetchInternal(key);
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            THLog.e(TAG, "Error requesting key " + key.toString(), retrofitError);
        }
        return getPositionsDTO;
    }

    protected GetPositionsDTO fetchInternal(OwnedPortfolioId key)
    {
        if (key instanceof PerPagedOwnedPortfolioId)
        {
            return positionService.get().getPositions(key.userId, key.portfolioId, ((PerPagedOwnedPortfolioId) key).page, ((PerPagedOwnedPortfolioId) key).perPage);
        }
        if (key instanceof PagedOwnedPortfolioId)
        {
            return positionService.get().getPositions(key.userId, key.portfolioId, ((PagedOwnedPortfolioId) key).page);
        }
        return positionService.get().getPositions(key.userId, key.portfolioId);
    }

    @Override public GetPositionsDTO get(OwnedPortfolioId key)
    {
        GetPositionsCutDTO getPositionsCutDTO = this.lruCache.get(key);
        if (getPositionsCutDTO == null)
        {
            return null;
        }
        return getPositionsCutDTO.create(key.portfolioId, securityCompactCache.get(), filedPositionCache.get());
    }

    @Override public GetPositionsDTO put(OwnedPortfolioId key, GetPositionsDTO value)
    {
        // We invalidate the previous list of positions before it get updated
        invalidateMatchingPositionCache(get(key));

        GetPositionsDTO previous = null;

        GetPositionsCutDTO previousCut = lruCache.put(
                key,
                new GetPositionsCutDTO(
                        value,
                        key.portfolioId,
                        securityCompactCache.get(),
                        filedPositionCache.get()));

        if (previousCut != null)
        {
            previous = previousCut.create(key.portfolioId, securityCompactCache.get(), filedPositionCache.get());
        }

        if (key != null)
        {
            portfolioCache.get().autoFetch(key);
        }

        return previous;
    }

    @Override public void invalidate(OwnedPortfolioId key)
    {
        invalidateMatchingPositionCache(get(key));
        lruCache.remove(key);
    }

    protected void invalidateMatchingPositionCache(GetPositionsDTO value)
    {
        if (value != null && value.positions != null)
        {
            for (PositionDTO positionDTO: value.positions)
            {
                filedPositionCache.get().invalidate(positionDTO.getOwnedPositionId());
            }
        }
    }

    private static class GetPositionsCutDTO
    {
        public List<OwnedPositionId> ownedPositionIds;
        public List<SecurityId> securityIds;
        public int openPositionsCount;
        public int closedPositionsCount;

        public GetPositionsCutDTO(
                GetPositionsDTO getPositionsDTO,
                Integer portfolioId,
                SecurityCompactCache securityCompactCache,
                PositionCache positionCache)
        {
            positionCache.put(portfolioId, getPositionsDTO.positions);
            this.ownedPositionIds = PositionDTO.getFiledPositionIds(portfolioId, getPositionsDTO.positions);

            securityCompactCache.put(getPositionsDTO.securities);
            this.securityIds = SecurityCompactDTO.getSecurityIds(getPositionsDTO.securities);
            
            this.openPositionsCount = getPositionsDTO.openPositionsCount;
            this.closedPositionsCount = getPositionsDTO.closedPositionsCount;
        }

        public GetPositionsDTO create(
                Integer portfolioId,
                SecurityCompactCache securityCompactCache,
                PositionCache positionCache)
        {
            return new GetPositionsDTO(
                    positionCache.get(ownedPositionIds),
                    securityCompactCache.get(securityIds),
                    openPositionsCount,
                    closedPositionsCount
            );
        }
    }
}
