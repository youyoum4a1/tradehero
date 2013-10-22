package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeDTOList;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.network.service.MarketService;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 7:20 PM To change this template use File | Settings | File Templates. */
@Singleton public class ExchangeListCache extends StraightDTOCache<ExchangeListType, ExchangeDTOList>
{
    public static final String TAG = ExchangeCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1; // Be careful to increase when necessary

    @Inject Lazy<MarketService> marketService;
    @Inject Lazy<ExchangeIdCache> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected ExchangeDTOList fetch(ExchangeListType key)
    {
        ExchangeDTOList exchangeDTOs = null;
        try
        {
            exchangeDTOs = new ExchangeDTOList(marketService.get().getExchanges());
        }
        catch (RetrofitError e)
        {
            THLog.e(TAG, "Failed to fetch key " + key.key, e);
        }
        return exchangeDTOs;
    }

    @Override public ExchangeDTOList put(ExchangeListType key, ExchangeDTOList value)
    {
        exchangeIdCache.get().put(value);
        return super.put(key, value);
    }
}
