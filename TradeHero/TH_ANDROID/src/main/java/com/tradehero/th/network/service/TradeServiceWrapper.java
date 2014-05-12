package com.tradehero.th.network.service;

import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class TradeServiceWrapper
{
    private final TradeService tradeService;
    private final TradeServiceAsync tradeServiceAsync;

    @Inject public TradeServiceWrapper(
            TradeService tradeService,
            TradeServiceAsync tradeServiceAsync)
    {
        super();
        this.tradeService = tradeService;
        this.tradeServiceAsync = tradeServiceAsync;
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
    {
        basicCheck(ownedPositionId);
        return this.tradeService.getTrades(ownedPositionId.userId, ownedPositionId.portfolioId, ownedPositionId.positionId);
    }

    public MiddleCallback<List<TradeDTO>> getTrades(OwnedPositionId ownedPositionId, Callback<List<TradeDTO>> callback)
    {
        basicCheck(ownedPositionId);
        MiddleCallback<List<TradeDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        this.tradeServiceAsync.getTrades(ownedPositionId.userId, ownedPositionId.portfolioId, ownedPositionId.positionId, middleCallback);
        return middleCallback;
    }

    public TradeDTO getTrade(OwnedTradeId ownedTradeId)
    {
        basicCheck(ownedTradeId);
        return this.tradeService.getTrade(ownedTradeId.userId, ownedTradeId.portfolioId, ownedTradeId.positionId, ownedTradeId.tradeId);
    }

    public MiddleCallback<TradeDTO> getTrade(OwnedTradeId ownedTradeId, Callback<TradeDTO> callback)
    {
        basicCheck(ownedTradeId);
        MiddleCallback<TradeDTO> middleCallback = new BaseMiddleCallback<>(callback);
        this.tradeServiceAsync.getTrade(ownedTradeId.userId, ownedTradeId.portfolioId, ownedTradeId.positionId, ownedTradeId.tradeId, middleCallback);
        return middleCallback;
    }
}
