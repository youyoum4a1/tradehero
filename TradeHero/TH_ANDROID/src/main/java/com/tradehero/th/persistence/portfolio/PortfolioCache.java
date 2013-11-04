package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.commons.io.IOUtils;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 3:28 PM To change this template use File | Settings | File Templates. */
@Singleton public class PortfolioCache extends StraightDTOCache<OwnedPortfolioId, PortfolioDTO>
{
    public static final String TAG = PortfolioCache.class.getName();
    public static final int DEFAULT_MAX_SIZE = 200;

    @Inject @Named("CurrentUser") protected UserBaseDTO currentUserBase;
    @Inject Lazy<PortfolioService> portfolioService;
    @Inject Lazy<PortfolioCompactCache> portfolioCompactCache;
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
        return portfolioService.get().getPortfolio(key.userId, key.portfolioId);
    }

    @Override public PortfolioDTO put(OwnedPortfolioId key, PortfolioDTO value)
    {
        if (value != null)
        {
            portfolioCompactCache.get().put(key.getPortfolioId(), value);
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

    public GetOrFetchTask<List<PortfolioDTO>> getOrFetchTask(
            final List<? extends OwnedPortfolioId> keys,
            Listener<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>> callback)
    {
        return getOrFetchTask(keys, false, callback);
    }

    public GetOrFetchTask<List<PortfolioDTO>> getOrFetchTask(
            final List<? extends OwnedPortfolioId> keys,
            final boolean force,
            Listener<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>> callback)
    {
        final WeakReference<Listener<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>>> weakCallback = new WeakReference<>(callback);

        return new GetOrFetchTask<List<PortfolioDTO>>()
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

            @Override protected void onPostExecute(List<PortfolioDTO> values)
            {
                super.onPostExecute(values);
                if (!hasForgottenListener() && !isCancelled())
                {
                    Listener<List<? extends OwnedPortfolioId>, List<? extends PortfolioDTO>> retrievedCallback = weakCallback.get();
                    if (retrievedCallback != null)
                    {
                        if (error != null)
                        {
                            retrievedCallback.onDTOReceived(keys, values);
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
