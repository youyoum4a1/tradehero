package com.tradehero.th.network.service;

import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes trade requests
 * Created by xavier on 12/12/13.
 */
public class TradeServiceWrapper
{
    public static final String TAG = TradeServiceWrapper.class.getSimpleName();

    @Inject TradeService tradeService;

    public TradeServiceWrapper()
    {
        super();
        DaggerUtils.inject(this);
    }

    private void basicCheck(OwnedPositionId ownedPositionId)
    {
        if (ownedPositionId == null)
        {
            throw new NullPointerException("ownedPositionId cannot be null");
        }
        if (ownedPositionId.userId == null)
        {
            throw new NullPointerException("ownedPositionId.userId cannot be null");
        }
        if (ownedPositionId.portfolioId == null)
        {
            throw new NullPointerException("ownedPositionId.portfolioId cannot be null");
        }
        if (ownedPositionId.positionId == null)
        {
            throw new NullPointerException("ownedPositionId.positionId cannot be null");
        }
    }

    private void basicCheck(OwnedTradeId ownedTradeId)
    {
        basicCheck((OwnedPositionId) ownedTradeId);
        if (ownedTradeId.tradeId == null)
        {
            throw new NullPointerException("ownedTradeId.tradeId cannot be null");
        }
    }

    public List<TradeDTO> getTrades(OwnedPositionId ownedPositionId)
            throws RetrofitError
    {
        basicCheck(ownedPositionId);
        return this.tradeService.getTrades(ownedPositionId.userId, ownedPositionId.portfolioId, ownedPositionId.positionId);
    }

    public void getTrades(OwnedPositionId ownedPositionId, Callback<List<TradeDTO>> callback)
            throws RetrofitError
    {
        basicCheck(ownedPositionId);
        this.tradeService.getTrades(ownedPositionId.userId, ownedPositionId.portfolioId, ownedPositionId.positionId, callback);
    }

    public TradeDTO getTrade(OwnedTradeId ownedTradeId)
            throws RetrofitError
    {
        basicCheck(ownedTradeId);
        return this.tradeService.getTrade(ownedTradeId.userId, ownedTradeId.portfolioId, ownedTradeId.positionId, ownedTradeId.tradeId);
    }

    public void getTrade(OwnedTradeId ownedTradeId, Callback<TradeDTO> callback)
            throws RetrofitError
    {
        basicCheck(ownedTradeId);
        this.tradeService.getTrade(ownedTradeId.userId, ownedTradeId.portfolioId, ownedTradeId.positionId, ownedTradeId.tradeId, callback);
    }
}
