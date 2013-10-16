package com.tradehero.th.persistence.position;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PagedOwnedPortfolioId;
import com.tradehero.th.api.portfolio.PerPagedOwnedPortfolioId;
import com.tradehero.th.api.position.FiledPositionId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.PositionService;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:44 PM To change this template use File | Settings | File Templates. */
@Singleton public class GetPositionsCache implements DTOCache<String, OwnedPortfolioId, GetPositionsDTO>
{
    public static final String TAG = GetPositionsCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    // We need to compose here, instead of inheritance, otherwise we get a compile error regarding erasure on put and put.
    private LruCache<String, GetPositionsCache.GetPositionsCutDTO> lruCache;
    @Inject protected Lazy<PositionService> positionService;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<FiledPositionCache> filedPositionCache;

    //<editor-fold desc="Constructors">
    @Inject public GetPositionsCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public GetPositionsCache(int maxSize)
    {
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

    @Override public GetPositionsDTO getOrFetch(OwnedPortfolioId key)
    {
        return getOrFetch(key, false);
    }

    @Override public GetPositionsDTO getOrFetch(OwnedPortfolioId key, boolean force)
    {
        GetPositionsCutDTO getPositionsCutDTO = lruCache.get(key.makeKey());
        GetPositionsDTO getPositionsDTO = null;

        if (force || getPositionsCutDTO == null)
        {
            getPositionsDTO = fetch(key);
            put(key, getPositionsDTO);
        }
        else
        {
            getPositionsDTO = getPositionsCutDTO.create(key.portfolioId, securityCompactCache.get(), filedPositionCache.get());
        }
        return getPositionsDTO;
    }

    @Override public AsyncTask<Void, Void, GetPositionsDTO> getOrFetch(OwnedPortfolioId key, Listener<OwnedPortfolioId, GetPositionsDTO> callback)
    {
        return getOrFetch(key, false, callback);
    }

    public AsyncTask<Void, Void, GetPositionsDTO> getOrFetch(final OwnedPortfolioId key, final boolean force, final Listener<OwnedPortfolioId, GetPositionsDTO> callback)
    {
        final WeakReference<Listener<OwnedPortfolioId, GetPositionsDTO>> weakCallback = new WeakReference<Listener<OwnedPortfolioId, GetPositionsDTO>>(callback);

        return new AsyncTask<Void, Void, GetPositionsDTO>()
        {
            @Override protected GetPositionsDTO doInBackground(Void... voids)
            {
                return getOrFetch(key, force);
            }

            @Override protected void onPostExecute(GetPositionsDTO value)
            {
                super.onPostExecute(value);
                Listener<OwnedPortfolioId, GetPositionsDTO> retrievedCallback = weakCallback.get();
                // We retrieve the callback right away to avoid having it vanish between the 2 get() calls.
                if (!isCancelled() && retrievedCallback != null)
                {
                    retrievedCallback.onDTOReceived(key, value);
                }
            }
        };
    }

    @Override public GetPositionsDTO get(OwnedPortfolioId key)
    {
        GetPositionsCutDTO getPositionsCutDTO = this.lruCache.get(key.makeKey());
        if (getPositionsCutDTO == null)
        {
            return null;
        }
        return getPositionsCutDTO.create(key.portfolioId, securityCompactCache.get(), filedPositionCache.get());
    }

    @Override public GetPositionsDTO put(OwnedPortfolioId key, GetPositionsDTO value)
    {
        GetPositionsDTO previous = null;

        GetPositionsCutDTO previousCut = lruCache.put(
                key.makeKey(),
                new GetPositionsCutDTO(
                        value,
                        key.portfolioId,
                        securityCompactCache.get(),
                        filedPositionCache.get()));

        if (previousCut != null)
        {
            previous = previousCut.create(key.portfolioId, securityCompactCache.get(), filedPositionCache.get());
        }

        return previous;
    }

    private static class GetPositionsCutDTO
    {
        public List<FiledPositionId> filedPositionIds ;
        public List<SecurityId> securityIds;
        public int openPositionsCount;
        public int closedPositionsCount;

        public GetPositionsCutDTO(
                GetPositionsDTO getPositionsDTO,
                Integer portfolioId,
                SecurityCompactCache securityCompactCache,
                FiledPositionCache filedPositionCache)
        {
            filedPositionCache.put(portfolioId, getPositionsDTO.positions);
            this.filedPositionIds = PositionDTO.getFiledPositionIds(portfolioId, getPositionsDTO.positions);

            securityCompactCache.put(getPositionsDTO.securities);
            this.securityIds = SecurityCompactDTO.getSecurityIds(getPositionsDTO.securities);
            
            this.openPositionsCount = getPositionsDTO.openPositionsCount;
            this.closedPositionsCount = getPositionsDTO.closedPositionsCount;
        }

        public GetPositionsDTO create(
                Integer portfolioId,
                SecurityCompactCache securityCompactCache,
                FiledPositionCache filedPositionCache)
        {
            return new GetPositionsDTO(
                    filedPositionCache.get(filedPositionIds),
                    securityCompactCache.get(securityIds),
                    openPositionsCount,
                    closedPositionsCount
            );
        }
    }

}
