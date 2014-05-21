package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class PortfolioCache extends StraightDTOCache<OwnedPortfolioId, PortfolioDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    @Inject Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @Inject Lazy<PortfolioCompactCache> portfolioCompactCache;
    @Inject PortfolioCompactListCache portfolioCompactListCache;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<GetPositionsCache> getPositionsCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected PortfolioDTO fetch(OwnedPortfolioId key) throws Throwable
    {
        return portfolioServiceWrapper.get().getPortfolio(key);
    }

    @Override public PortfolioDTO put(OwnedPortfolioId key, PortfolioDTO value)
    {
        if (value != null)
        {
            portfolioCompactCache.get().put(key.getPortfolioIdKey(), value);
            getPositionsCache.get().invalidate(key);
        }

        return super.put(key, value);
    }

    public List<PortfolioDTO> get(List<? extends OwnedPortfolioId> keys)
    {
        if (keys == null)
        {
            return null;
        }
        List<PortfolioDTO> values = new ArrayList<>();
        for (OwnedPortfolioId key: keys)
        {
            values.add(get(key));
        }
        return values;
    }

    public List<PortfolioDTO> getOrFetch(List<? extends OwnedPortfolioId> keys) throws Throwable
    {
        if (keys == null)
        {
            return null;
        }
        List<PortfolioDTO> values = new ArrayList<>();
        for (OwnedPortfolioId key: keys)
        {
            values.add(getOrFetch(key));
        }
        return values;
    }

    @Override public void invalidate(OwnedPortfolioId key)
    {
        super.invalidate(key);
        getPositionsCache.get().invalidate(key);
        portfolioCompactListCache.autoFetch(key.getUserBaseKey(), true);
    }

    public GetOrFetchTask<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>> getOrFetchTask(
            final List<? extends OwnedPortfolioId> keys,
            Listener<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>> callback)
    {
        return getOrFetchTask(keys, false, callback);
    }

    public GetOrFetchTask<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>> getOrFetchTask(
            final List<? extends OwnedPortfolioId> keys,
            final boolean force,
            Listener<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>> callback)
    {
        final WeakReference<Listener<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>>> weakCallback = new WeakReference<>(callback);

        return new GetOrFetchTask<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>>(callback)
        {
            Throwable error = null;

            @Override protected List<PortfolioDTO> doInBackground(Void... voids)
            {
                if (keys == null)
                {
                    return null;
                }
                List<PortfolioDTO> values = new ArrayList<>();
                try
                {
                    for (OwnedPortfolioId key: keys)
                    {
                        values.add(getOrFetch(key, force));
                    }
                }
                catch (Throwable throwable)
                {
                    error = throwable;
                }
                return values;
            }

            @Override protected void onPostExecute(List<? extends PortfolioDTO> values)
            {
                super.onPostExecute(values);
                if (!isCancelled())
                {
                    Listener<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>> retrievedCallback = weakCallback.get();
                    if (retrievedCallback != null)
                    {
                        if (error != null)
                        {
                            retrievedCallback.onDTOReceived(keys, values, !force);
                        }
                        else
                        {
                            retrievedCallback.onErrorThrown(keys, error);
                        }
                    }
                    if (error == null && keys != null)
                    {
                        for (OwnedPortfolioId key: keys)
                        {
                            pushToListeners(key);
                        }
                    }
                }
            }
        };
    }
}
