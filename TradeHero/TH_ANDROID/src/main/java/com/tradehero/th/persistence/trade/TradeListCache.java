package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.TradeService;
import dagger.Lazy;
import retrofit.RetrofitError;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by julien on 22/10/13
 */
public class TradeListCache extends StraightDTOCache<String, OwnedPositionId, List<OwnedTradeId>>
{
    public static final String TAG = TradeListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected Lazy<TradeService> tradeService;
    @Inject protected Lazy<TradeCache> tradeCache;

    //<editor-fold desc="Constructors">
    @Inject public TradeListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected List<OwnedTradeId> fetch(OwnedPositionId key)
    {
        THLog.d(TAG, "fetch " + key);
        try
        {
            return putInternal(key, tradeService.get().getTrades(key.userId, key.portfolioId, key.positionId));
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            THLog.e(TAG, "Error requesting key " + key.toString(), retrofitError);
        }
        return null;
    }

    protected List<OwnedTradeId> putInternal(OwnedPositionId key, List<TradeDTO> fleshedValues)
    {
        List<OwnedTradeId> tradeIds = null;
        if (fleshedValues != null)
        {
            tradeIds = new ArrayList<>();
            OwnedTradeId tradeId;
            for(TradeDTO trade: fleshedValues)
            {
                tradeId = new OwnedTradeId(key, trade.id);
                tradeIds.add(tradeId);
                tradeCache.get().put(trade.getTradeId(), trade);
            }
            put(key, tradeIds);
        }
        return tradeIds;
    }
}
