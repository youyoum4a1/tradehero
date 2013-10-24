package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.network.service.PortfolioService;
import dagger.Lazy;
import java.io.IOException;
import java.io.StringWriter;
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

    private Map<OwnedPortfolioId, Boolean> allOtherUserKeys;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCache()
    {
        super(DEFAULT_MAX_SIZE);
        allOtherUserKeys = new HashMap<>();
    }
    //</editor-fold>

    @Override protected PortfolioDTO fetch(OwnedPortfolioId key)
    {
        PortfolioDTO fetched = null;
        try
        {
            fetched = portfolioService.get().getPortfolio(key.userId, key.portfolioId);
        }
        catch (RetrofitError e)
        {
            THLog.e(TAG, "Failed to fetch key " + key, e);
            try
            {
                StringWriter writer = new StringWriter();
                IOUtils.copy(e.getResponse().getBody().in(), writer, "UTF-8");
                THLog.d(TAG, e.getUrl() + " -> " + writer.toString());
            }
            catch (IOException e2)
            {

            }
        }
        return fetched;
    }

    @Override public PortfolioDTO put(OwnedPortfolioId key, PortfolioDTO value)
    {
        if (value != null)
        {
            portfolioCompactCache.get().put(key.getPortfolioId(), value);
        }

        addOtherUserKey(key);

        return super.put(key, value);
    }

    private void addOtherUserKey(OwnedPortfolioId key)
    {
        if (!allOtherUserKeys.containsKey(key) && !key.getUserBaseKey().equals(currentUserBase.getBaseKey()))
        {
            allOtherUserKeys.put(key, true);
        }
    }

    public List<OwnedPortfolioId> getAllOtherUserKeys()
    {
        return new ArrayList<>(allOtherUserKeys.keySet());
    }
}
